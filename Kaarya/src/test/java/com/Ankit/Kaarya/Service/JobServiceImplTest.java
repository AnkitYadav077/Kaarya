package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.JobRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepo jobRepo;

    @Mock
    private IndustryRepo industryRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    void createJob_Success() {
        // Arrange
        JobsDto jobsDto = new JobsDto();
        Integer industryId = 1;
        Industry industry = new Industry();
        Jobs job = new Jobs();
        Jobs savedJob = new Jobs();
        JobsDto resultDto = new JobsDto();

        when(industryRepo.findById(industryId)).thenReturn(Optional.of(industry));
        when(modelMapper.map(jobsDto, Jobs.class)).thenReturn(job);
        when(jobRepo.save(job)).thenReturn(savedJob);
        when(modelMapper.map(savedJob, JobsDto.class)).thenReturn(resultDto);

        // Act
        JobsDto result = jobService.createJob(jobsDto, industryId);

        // Assert
        assertNotNull(result);
        verify(jobRepo).save(job);
    }

    @Test
    void getJobById_Exists_ReturnsJobDto() {
        // Arrange
        Integer jobId = 1;
        Jobs job = new Jobs();
        JobsDto dto = new JobsDto();

        when(jobRepo.findById(jobId)).thenReturn(Optional.of(job));
        when(modelMapper.map(job, JobsDto.class)).thenReturn(dto);

        // Act
        JobsDto result = jobService.getJobById(jobId);

        // Assert
        assertNotNull(result);
        verify(jobRepo).findById(jobId);
    }

    @Test
    void getJobById_NotFound_ThrowsException() {
        // Arrange
        Integer jobId = 1;
        when(jobRepo.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.getJobById(jobId);
        });
    }

    @Test
    void getJobsByWorkDate_ReturnsJobs() {
        // Arrange
        LocalDate date = LocalDate.now();
        Jobs job1 = new Jobs();
        Jobs job2 = new Jobs();
        List<Jobs> jobs = Arrays.asList(job1, job2);

        when(jobRepo.findByWorkDateBetween(any(), any())).thenReturn(jobs);
        when(modelMapper.map(any(), any())).thenReturn(new JobsDto());

        // Act
        List<JobsDto> result = jobService.getJobsByWorkDate(date);

        // Assert
        assertEquals(2, result.size());
        verify(jobRepo).findByWorkDateBetween(any(), any());
    }

    @Test
    void filterJobs_WithRadius_ReturnsFilteredJobs() {
        // Arrange
        String title = "Test Job";
        LocalDate workDate = LocalDate.now();
        Location location = new Location();
        Double radiusKm = 10.0;

        Industry industry = new Industry();
        industry.setIndustryId(1L);
        List<Industry> nearbyIndustries = Arrays.asList(industry);

        Jobs job = new Jobs();
        List<Jobs> filteredJobs = Arrays.asList(job);

        when(locationService.getNearbyIndustries(location, radiusKm)).thenReturn(nearbyIndustries);
        when(jobRepo.findFilteredJobs(title, workDate.atStartOfDay(), Arrays.asList(1L))).thenReturn(filteredJobs);
        when(modelMapper.map(any(), any())).thenReturn(new JobsDto());

        // Act
        List<JobsDto> result = jobService.filterJobs(title, workDate, location, radiusKm);

        // Assert
        assertEquals(1, result.size());
        verify(locationService).getNearbyIndustries(location, radiusKm);
    }

    @Test
    void filterJobs_InvalidRadius_ThrowsException() {
        // Arrange
        Location location = new Location();
        Double radiusKm = -1.0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jobService.filterJobs(null, null, location, radiusKm);
        });
    }
}