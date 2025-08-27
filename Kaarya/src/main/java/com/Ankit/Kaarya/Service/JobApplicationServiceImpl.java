package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.*;
import com.Ankit.Kaarya.Exceptions.InvalidStatusException;
import com.Ankit.Kaarya.Exceptions.PaymentProcessingException;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.JobApplicationDto;
import com.Ankit.Kaarya.Payloads.PaymentDto;
import com.Ankit.Kaarya.Payloads.UserApplicationStatsDto;
import com.Ankit.Kaarya.Payloads.UserJobApplicationSummary;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Repo.JobRepo;
import com.Ankit.Kaarya.Repo.PaymentRepo;
import com.Ankit.Kaarya.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepo jobApplicationRepo;
    private final JobRepo jobRepo;
    private final ChatService chatService;
    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;

    @Override
    @CacheEvict(value = {"userApplications", "applicationStats"}, key = "#userId")
    public JobApplicationDto applyForJob(Long jobId, Long userId) {
        List<ApplicationStatus> activeStatuses = Arrays.asList(
                ApplicationStatus.PENDING,
                ApplicationStatus.APPROVED
        );

        if (jobApplicationRepo.existsByUsersUserIdAndStatusIn(userId, activeStatuses)) {
            throw new RuntimeException("You already have an active job application. Please wait until it's resolved.");
        }

        Jobs job = jobRepo.findById(Math.toIntExact(jobId))
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        Users user = userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        JobApplication application = new JobApplication();
        application.setJobs(job);
        application.setUsers(user);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);
        application.setIndustry(job.getIndustry());
        application.setPayAmount(job.getPayAmount());

        JobApplication saved = jobApplicationRepo.save(application);
        return convertToDto(saved);
    }

    @Override
    @CacheEvict(value = {"applications", "userApplications"}, allEntries = true)
    public JobApplicationDto approveApplication(Long applicationId) {
        JobApplication application = jobApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        Jobs job = application.getJobs();
        int maxAllowed = job.getRequiredWorkers();
        int approvedCount = jobApplicationRepo.countApprovedApplicationsByJobId(job.getJobId());

        if (approvedCount >= maxAllowed) {
            List<JobApplication> pendingApplications = jobApplicationRepo.findByJobsJobIdAndStatus(
                    job.getJobId(), ApplicationStatus.PENDING);

            for (JobApplication app : pendingApplications) {
                app.setStatus(ApplicationStatus.REJECTED);
                jobApplicationRepo.save(app);
            }

            throw new RuntimeException("All positions filled. Application rejected");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        JobApplication saved = jobApplicationRepo.save(application);
        chatService.createChatRoom(application);
        return convertToDto(saved);
    }

    @Override
    @CacheEvict(value = {"applications", "userApplications"}, allEntries = true)
    public JobApplicationDto completeApplication(Long applicationId) {
        JobApplication application = jobApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new InvalidStatusException(
                    "Only APPROVED applications can be completed. Current status: " + application.getStatus()
            );
        }

        application.setStatus(ApplicationStatus.COMPLETED);
        JobApplication saved = jobApplicationRepo.save(application);
        return convertToDto(saved);
    }

    @Override
    @Cacheable(value = "applications", key = "#status.name()")
    public List<JobApplicationDto> getApplicationsByStatus(ApplicationStatus status) {
        List<JobApplication> applications = jobApplicationRepo.findByStatus(status);
        return applications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"applications", "userApplications"}, allEntries = true)
    public JobApplicationDto rejectApplication(Long applicationId) {
        JobApplication application = jobApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        application.setStatus(ApplicationStatus.REJECTED);
        JobApplication saved = jobApplicationRepo.save(application);
        return convertToDto(saved);
    }

    @Override
    @CacheEvict(value = {"applications", "userApplications"}, allEntries = true)
    public PaymentDto initiatePayment(Long applicationId) {
        JobApplication application = jobApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        if (application.getStatus() != ApplicationStatus.COMPLETED) {
            throw new InvalidStatusException(
                    "Only COMPLETED applications can be paid. Current status: " + application.getStatus()
            );
        }

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setAmount(application.getPayAmount());
        paymentDto.setCurrency("INR");
        paymentDto.setIndustryId(application.getJobs().getIndustry().getIndustryId());
        paymentDto.setJobApplicationId(applicationId);

        try {
            PaymentDto createdPayment = paymentService.createPaymentOrder(paymentDto);

            Payment payment = modelMapper.map(createdPayment, Payment.class);
            payment.setJobApplication(application);
            paymentRepo.save(payment);

            application.setStatus(ApplicationStatus.PAYMENT_DONE);
            jobApplicationRepo.save(application);

            createdPayment.setPaymentId(payment.getPaymentId());
            return createdPayment;
        } catch (Exception e) {
            throw new PaymentProcessingException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "completedApplications", key = "#userId")
    public long getCompletedApplicationsCountByUserId(Long userId) {
        int userIdInt = Math.toIntExact(userId);
        return jobApplicationRepo.countByUsers_UserIdAndStatusIn(
                userIdInt,
                Arrays.asList(
                        ApplicationStatus.COMPLETED,
                        ApplicationStatus.PAYMENT_IN_PROGRESS,
                        ApplicationStatus.PAYMENT_DONE
                )
        );
    }

    @Override
    @Cacheable(value = "applicationStats", key = "#userId")
    public UserApplicationStatsDto getUserApplicationStats(Long userId) {
        int userIdInt = Math.toIntExact(userId);

        int totalApplications = Math.toIntExact(jobApplicationRepo.countByUsers_UserId(userIdInt));
        int rejectedCount = Math.toIntExact(jobApplicationRepo.countByUsers_UserIdAndStatus(userIdInt, ApplicationStatus.REJECTED));
        int approvedCount = totalApplications - rejectedCount;

        return new UserApplicationStatsDto(totalApplications, rejectedCount, approvedCount);
    }

    @Override
    @Cacheable(value = "userApplications", key = "#userId")
    public List<UserJobApplicationSummary> getUserJobApplications(Long userId) {
        int userIdInt = Math.toIntExact(userId);
        List<JobApplication> applications = jobApplicationRepo.findByUserIdWithJobAndIndustry(userIdInt);

        return applications.stream()
                .map(app -> {
                    Jobs job = app.getJobs();
                    Industry industry = job.getIndustry();

                    return new UserJobApplicationSummary(
                            job.getTitle(),
                            industry.getName(),
                            app.getStatus().name(),
                            app.getAppliedDate()
                    );
                })
                .collect(Collectors.toList());
    }

    private JobApplicationDto convertToDto(JobApplication application) {
        JobApplicationDto dto = modelMapper.map(application, JobApplicationDto.class);
        dto.setJobId((long) Math.toIntExact(application.getJobs().getJobId()));
        dto.setUserId((long) Math.toIntExact(application.getUsers().getUserId()));
        dto.setStatus(application.getStatus().name());
        dto.setPayAmount(application.getPayAmount());
        return dto;
    }
}