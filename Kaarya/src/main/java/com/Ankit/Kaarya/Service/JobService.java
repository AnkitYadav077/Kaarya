package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Payloads.JobsDto;

import java.time.LocalDate;
import java.util.List;

public interface JobService {
    JobsDto createJob(JobsDto jobs, Integer industryId);
    JobsDto updateJob(JobsDto jobs, Integer jobId);
    JobsDto getJobById(Integer jobId);
    JobsDto deleteJob(Integer jobId);
    List<JobsDto> getJobsByWorkDate(LocalDate date);
    List<JobsDto> getAllJobByIndustry(Integer industryId);
    List<Jobs> findAll();
    JobsDto jobToDto(Jobs job);

    public List<JobsDto> filterJobs(String title, LocalDate workDate, Location location, Double radiusKm);

}