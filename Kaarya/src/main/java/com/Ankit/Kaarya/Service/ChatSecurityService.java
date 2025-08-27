package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSecurityService {

    private final JobApplicationRepo jobApplicationRepo;

    public boolean hasAccessToRoom(String roomId, Authentication auth) {
        try {
            if (!roomId.startsWith("chat_")) {
                return false;
            }

            Long applicationId = Long.parseLong(roomId.split("_")[1]);
            JobApplication application = jobApplicationRepo.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            Long authenticatedId = ((OtpAuthenticationToken) auth).getId();


            if (application.getUsers() != null &&
                    application.getUsers().getUserId().equals(authenticatedId)) {
                return true;
            }


            if (application.getJobs() != null &&
                    application.getJobs().getIndustry() != null &&
                    application.getJobs().getIndustry().getIndustryId().equals(authenticatedId)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}