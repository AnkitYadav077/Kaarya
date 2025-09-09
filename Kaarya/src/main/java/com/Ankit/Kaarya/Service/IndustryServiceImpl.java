package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.ApplicantWithTeamSizeDto;
import com.Ankit.Kaarya.Payloads.IndustryDto;
import com.Ankit.Kaarya.Payloads.IndustryJobApplicationsDto;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepo industryRepo;
    private final ModelMapper modelMapper;
    private final ImageService imageService;
    private final JobApplicationRepo jobApplicationRepo;

    @Override
    @CacheEvict(value = "industries", allEntries = true)
    public IndustryDto registerIndustry(IndustryDto industryDto, String role) {
        try {
            // Check if email already exists
            if (industryRepo.existsByEmail(industryDto.getEmail())) {
                throw new RuntimeException("Email already registered: " + industryDto.getEmail());
            }

            Industry industry = dtoToIndustry(industryDto);
            industry.setRole(role);
            Industry savedIndustry = industryRepo.save(industry);
            return industryToDto(savedIndustry);
        } catch (Exception e) {
            throw new RuntimeException("Error registering industry: " + e.getMessage(), e);
        }
    }


    @Override
    @Cacheable(value = "industry", key = "#industryId")
    public IndustryDto getProfile(Integer industryId) {
        Industry industry = industryRepo.findById(industryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", industryId));
        return industryToDto(industry);
    }

    @Override
    @CacheEvict(value = {"industry", "industries"}, key = "#industryId")
    public IndustryDto updateIndustry(Integer industryId, IndustryDto industryDto) {
        Industry industry = industryRepo.findById(industryId)
                .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", industryId));

        industry.setName(industryDto.getName());
        industry.setEmail(industryDto.getEmail());

        Industry updatedIndustry = industryRepo.save(industry);
        return industryToDto(updatedIndustry);
    }

    @Override
    @Cacheable(value = "industryApplications", key = "#industryId")
    public IndustryDto getUsersAppliedForEachJobByIndustry(Long industryId) {
        Industry industry = industryRepo.findById(Math.toIntExact(industryId))
                .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", industryId));

        IndustryDto industryDto = new IndustryDto();
        industryDto.setIndustryId(industry.getIndustryId());
        industryDto.setName(industry.getName());
        industryDto.setEmail(industry.getEmail());
        industryDto.setPhoneNo(industry.getPhoneNo());

        List<IndustryJobApplicationsDto> jobList = new ArrayList<>();

        for (Jobs job : industry.getJobs()) {
            List<JobApplication> applications = jobApplicationRepo.findByJobs_JobId(job.getJobId());

            List<ApplicantWithTeamSizeDto> applicants = applications.stream()
                    .map(app -> {
                        Users user = app.getUsers();
                        return new ApplicantWithTeamSizeDto(
                                user.getUserId(),
                                user.getName(),
                                user.getPhoneNo(),
                                user.getUpiId(),
                                app.getStatus().toString()
                        );
                    }).collect(Collectors.toList());

            IndustryJobApplicationsDto jobDetails = new IndustryJobApplicationsDto(
                    job.getTitle(),
                    job.getDescription(),
                    job.getPayAmount(),
                    job.getRequiredWorkers(),
                    applicants
            );

            jobList.add(jobDetails);
        }

        industryDto.setJobApplications(jobList);
        return industryDto;
    }

    @Override
    @CacheEvict(value = {"industry", "industries"}, key = "#industryId")
    public IndustryDto uploadIndustryImage(Long industryId, MultipartFile file) {
        try {
            Industry industry = industryRepo.findById(industryId.intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", industryId));

            String imageUrl = imageService.uploadImage(file);
            industry.setImageUrl(imageUrl);
            Industry updatedIndustry = industryRepo.save(industry);
            return industryToDto(updatedIndustry);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading industry image: " + e.getMessage(), e);
        }
    }

    public IndustryDto industryToDto(Industry industry) {
        IndustryDto dto = modelMapper.map(industry, IndustryDto.class);
        dto.setRole(industry.getRole());
        return dto;
    }

    public Industry dtoToIndustry(IndustryDto industryDto) {
        return modelMapper.map(industryDto, Industry.class);
    }
}