package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Entity.ApplicationStatus;
import com.Ankit.Kaarya.Payloads.JobApplicationDto;
import com.Ankit.Kaarya.Payloads.PaymentDto;
import com.Ankit.Kaarya.Payloads.UserApplicationStatsDto;
import com.Ankit.Kaarya.Payloads.UserJobApplicationSummary;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobApplication")
@RequiredArgsConstructor
public class JobApplicationController {


    private final JobApplicationService jobApplicationService;

    private Long getAuthenticatedUserId() {
        OtpAuthenticationToken auth =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getId();
    }

    private Long getAuthenticatedIndustryId() {
        OtpAuthenticationToken auth =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getId();
    }

    @PostMapping("/{jobId}/apply")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<JobApplicationDto> apply(@PathVariable Long jobId) {
        Long userId = getAuthenticatedUserId();
        JobApplicationDto response = jobApplicationService.applyForJob(jobId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{applicationId}/approve")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<JobApplicationDto> approve(@PathVariable Long applicationId) {
        JobApplicationDto response = jobApplicationService.approveApplication(applicationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{applicationId}/reject")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<JobApplicationDto> reject(@PathVariable Long applicationId) {
        JobApplicationDto response = jobApplicationService.rejectApplication(applicationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{applicationId}/complete")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<JobApplicationDto> complete(@PathVariable Long applicationId) {
        JobApplicationDto response = jobApplicationService.completeApplication(applicationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplicationDto>> getByStatus(
            @PathVariable ApplicationStatus status) {
        List<JobApplicationDto> applications = jobApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @PostMapping("/{applicationId}/initiate-payment")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<PaymentDto> initiatePayment(@PathVariable Long applicationId) {
        PaymentDto paymentDto = jobApplicationService.initiatePayment(applicationId);
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/completed-count")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Long>> getCompletedCountByUser() {
        Long userId = getAuthenticatedUserId();
        long count = jobApplicationService.getCompletedApplicationsCountByUserId(userId);
        return ResponseEntity.ok(Collections.singletonMap("completedCount", count));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserApplicationStatsDto> getUserApplicationStats() {
        Long userId = getAuthenticatedUserId();
        UserApplicationStatsDto stats = jobApplicationService.getUserApplicationStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/job-applications")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<UserJobApplicationSummary>> getUserJobApplications() {
        Long userId = getAuthenticatedUserId();
        List<UserJobApplicationSummary> applications = jobApplicationService.getUserJobApplications(userId);
        return ResponseEntity.ok(applications);
    }
}