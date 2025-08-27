package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Entity.ApplicationStatus;
import com.Ankit.Kaarya.Entity.ChatRoom;
import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Payloads.ChatRoomDto;
import com.Ankit.Kaarya.Repo.ChatRoomRepo;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {


    private final JobApplicationRepo jobApplicationRepo;


    private final ChatRoomRepo chatRoomRepo;

    private Long getAuthenticatedId() {
        return ((OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getId();
    }

    @GetMapping("/user/rooms")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<ChatRoomDto> getUserChatRooms() {
        Long userId = getAuthenticatedId();
        List<JobApplication> applications = jobApplicationRepo
                .findByUsers_UserIdAndStatus(userId, ApplicationStatus.APPROVED);

        return applications.stream().map(app -> {
            ChatRoom room = chatRoomRepo.findByApplication(app)
                    .orElseThrow(() -> new RuntimeException("Chat room not found"));
            return new ChatRoomDto(
                    room.getRoomId(),
                    app.getJobs().getIndustry().getName(),
                    app.getJobs().getTitle()
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/industry/rooms")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public List<ChatRoomDto> getIndustryChatRooms() {
        Long industryId = getAuthenticatedId();
        List<JobApplication> applications = jobApplicationRepo
                .findApprovedApplicationsByIndustry(industryId);

        return applications.stream().map(app -> {
            ChatRoom room = chatRoomRepo.findByApplication(app)
                    .orElseThrow(() -> new RuntimeException("Chat room not found"));
            return new ChatRoomDto(
                    room.getRoomId(),
                    app.getUsers().getName(),
                    app.getJobs().getTitle()
            );
        }).collect(Collectors.toList());
    }
}