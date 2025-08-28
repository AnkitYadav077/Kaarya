package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Exceptions.ChatAccessDeniedException;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
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
            // Extract application ID from the roomId format "chat_<applicationId>"
            if (!roomId.startsWith("chat_")) {
                throw new ChatAccessDeniedException("Invalid room ID format: " + roomId);
            }

            Long applicationId = Long.parseLong(roomId.substring(5)); // Remove "chat_" prefix
            JobApplication application = jobApplicationRepo.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

            Long authenticatedId = ((OtpAuthenticationToken) auth).getId();

            // Check if authenticated user is the applicant
            if (application.getUsers() != null &&
                    application.getUsers().getUserId().equals(authenticatedId)) {
                return true;
            }

            // Check if authenticated user is the industry owner
            if (application.getJobs() != null &&
                    application.getJobs().getIndustry() != null &&
                    application.getJobs().getIndustry().getIndustryId().equals(authenticatedId)) {
                return true;
            }

            throw new ChatAccessDeniedException("User does not have access to this chat room");
        } catch (ResourceNotFoundException | ChatAccessDeniedException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new ChatAccessDeniedException("Invalid room ID format: " + roomId);
        } catch (Exception e) {
            throw new ChatAccessDeniedException("Error validating chat room access: " + e.getMessage());
        }
    }
}