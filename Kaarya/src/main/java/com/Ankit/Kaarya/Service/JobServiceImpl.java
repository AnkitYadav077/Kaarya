package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.JobRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepo jobRepo;
    private final IndustryRepo industryRepo;
    private final ModelMapper modelMapper;
    private final LocationService locationService;

    @Override
    @CacheEvict(value = {"jobs", "industryJobs"}, allEntries = true)
    public JobsDto createJob(JobsDto jobsDto, Integer industryId) {
        Industry industry = industryRepo.findById(industryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", industryId));

        Jobs jobs = dtoToJob(jobsDto);
        jobs.setIndustry(industry);
        jobs.setCreatedAt(LocalDateTime.now());

        Jobs savedJob = jobRepo.save(jobs);
        return jobToDto(savedJob);
    }

    @Override
    @CacheEvict(value = {"jobs", "job", "industryJobs"}, key = "#jobId")
    public JobsDto updateJob(JobsDto jobsDto, Integer jobId) {
        Jobs jobs = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobs", "id", jobId));

        jobs.setTitle(jobsDto.getTitle());
        jobs.setDescription(jobsDto.getDescription());
        jobs.setPayAmount(jobsDto.getPayAmount());
        jobs.setRequiredWorkers(jobsDto.getRequiredWorkers());
        jobs.setWorkDate(jobsDto.getWorkDate());
        jobs.setCreatedAt(LocalDateTime.now());

        Jobs updatedJobs = jobRepo.save(jobs);
        return jobToDto(updatedJobs);
    }

    @Override
    @Cacheable(value = "job", key = "#jobId")
    public JobsDto getJobById(Integer jobId) {
        Jobs jobs = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobs", "id", jobId));
        return jobToDto(jobs);
    }

    @Override
    @CacheEvict(value = {"jobs", "job", "industryJobs"}, key = "#jobId")
    public JobsDto deleteJob(Integer jobId) {
        Jobs jobs = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobs", "id", jobId));

        jobRepo.delete(jobs);
        return jobToDto(jobs);
    }

    @Override
    @Cacheable(value = "jobsByDate", key = "#date.toString()")
    public List<JobsDto> getJobsByWorkDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Jobs> jobs = jobRepo.findByWorkDateBetween(startOfDay, endOfDay);
        return jobs.stream()
                .map(this::jobToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "industryJobs", key = "#industryId")
    public List<JobsDto> getAllJobByIndustry(Integer industryId) {
        Industry industry = industryRepo.findById(industryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", industryId));

        List<Jobs> jobs = jobRepo.findByIndustry(industry);
        return jobs.stream()
                .map(job -> modelMapper.map(job, JobsDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "filteredJobs", key = "{#title, #workDate, #location, #radiusKm}")
    public List<JobsDto> filterJobs(String title, LocalDate workDate, Location location, Double radiusKm) {
        // Prevent negative or zero radius
        if (radiusKm != null && radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0 km");
        }

        LocalDateTime workDateTime = (workDate != null) ? workDate.atStartOfDay() : null;
        List<Long> industryIds = null;

        if (radiusKm != null && radiusKm > 0) {
            List<Industry> nearbyIndustries = locationService.getNearbyIndustries(location, radiusKm);
            industryIds = nearbyIndustries.stream()
                    .map(Industry::getIndustryId)
                    .collect(Collectors.toList());

            if (industryIds.isEmpty()) {
                return new ArrayList<>();
            }
        }

        List<Jobs> filteredJobs = jobRepo.findFilteredJobs(title, workDateTime, industryIds);
        return filteredJobs.stream()
                .map(this::jobToDto)
                .collect(Collectors.toList());
    }


    @Override
    @Cacheable(value = "jobs")
    public List<Jobs> findAll() {
        return jobRepo.findAll();
    }

    @Override
    public JobsDto jobToDto(Jobs job) {
        return modelMapper.map(job, JobsDto.class);
    }

    private Jobs dtoToJob(JobsDto dto) {
        return modelMapper.map(dto, Jobs.class);
    }
}