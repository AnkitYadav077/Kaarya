package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Payloads.IndustryJobsResponseDto;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobsController {

    private final JobService service;
    private final LocationService locationService;
    private final JobVisibilityConsumer jobVisibilityConsumer;
    private final UserService userService;
    private final RedisLocationService redisLocationService;

    private Long getAuthenticatedIndustryId() {
        OtpAuthenticationToken auth =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getId();
    }

    private Long getAuthenticatedUserId() {
        OtpAuthenticationToken auth =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getId();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<JobsDto> newJobs(@Valid @RequestBody JobsDto jobsDto) {
        Long industryId = getAuthenticatedIndustryId();
        JobsDto createdJob = service.createJob(jobsDto, industryId.intValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<JobsDto> updatedJobs(@RequestBody JobsDto jobsDto, @PathVariable Integer jobId) {
        JobsDto updatedJob = service.updateJob(jobsDto, jobId);
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping("/{jobId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_INDUSTRY')")
    public ResponseEntity<JobsDto> getById(@PathVariable Integer jobId) {
        JobsDto job = service.getJobById(jobId);
        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<JobsDto> deleteJobs(@PathVariable Integer jobId) {
        JobsDto deletedJob = service.deleteJob(jobId);
        return ResponseEntity.ok(deletedJob);
    }

    @GetMapping("/date/by-date")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_INDUSTRY')")
    public ResponseEntity<List<JobsDto>> getByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<JobsDto> jobs = service.getJobsByWorkDate(date);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/industry/jobs")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_INDUSTRY')")
    public ResponseEntity<List<JobsDto>> getAllJobByIndustry() {
        Long industryId = getAuthenticatedIndustryId();
        List<JobsDto> jobs = service.getAllJobByIndustry(industryId.intValue());
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/visible/jobs/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<IndustryJobsResponseDto>> getVisibleJobsNearbyUser() {
        Long userId = getAuthenticatedUserId();

        Location currentLocation = redisLocationService.getUserLocation(userId);
        if (currentLocation == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        Set<Long> visibleJobIds = jobVisibilityConsumer.getAllVisibleJobs();
        List<Industry> nearbyIndustries = locationService.getNearbyIndustries(currentLocation, 10.0);
        Set<Long> nearbyIndustryIds = nearbyIndustries.stream()
                .map(Industry::getIndustryId)
                .collect(Collectors.toSet());

        List<Jobs> allJobs = service.findAll();

        // Group jobs by industry
        Map<Industry, List<JobsDto>> industryJobsMap = allJobs.stream()
                .filter(job -> visibleJobIds.contains(job.getJobId()))
                .filter(job -> job.getIndustry() != null &&
                        nearbyIndustryIds.contains(job.getIndustry().getIndustryId()))
                .collect(Collectors.groupingBy(
                        Jobs::getIndustry,
                        Collectors.mapping(service::jobToDto, Collectors.toList())
                ));

        // Convert to response DTO
        List<IndustryJobsResponseDto> result = industryJobsMap.entrySet().stream()
                .map(entry -> new IndustryJobsResponseDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }


    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<List<JobsDto>> filterJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam(required = false) Double radiusKm) {

        Long userId = getAuthenticatedUserId(); // Your method to get user ID
        Users user = userService.getUserById(userId);

        if (user.getLocation() == null) {
            throw new IllegalArgumentException("User has no location set");
        }

        Location location = user.getLocation();

        List<JobsDto> filteredJobs = service.filterJobs(title, workDate, location, radiusKm);
        return ResponseEntity.ok(filteredJobs);
    }

}