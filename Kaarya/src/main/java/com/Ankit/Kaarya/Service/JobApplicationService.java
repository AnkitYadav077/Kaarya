package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.ApplicationStatus;
import com.Ankit.Kaarya.Payloads.JobApplicationDto;
import com.Ankit.Kaarya.Payloads.PaymentDto;
import com.Ankit.Kaarya.Payloads.UserApplicationStatsDto;
import com.Ankit.Kaarya.Payloads.UserJobApplicationSummary;

import java.util.List;

public interface JobApplicationService {
    JobApplicationDto applyForJob(Long jobId, Long userId);
    JobApplicationDto approveApplication(Long applicationId);
    JobApplicationDto rejectApplication(Long applicationId);
    JobApplicationDto completeApplication(Long applicationId);
    List<JobApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    PaymentDto initiatePayment(Long applicationId);
    long getCompletedApplicationsCountByUserId(Long userId);
    UserApplicationStatsDto getUserApplicationStats(Long userId);
    List<UserJobApplicationSummary> getUserJobApplications(Long userId);
}