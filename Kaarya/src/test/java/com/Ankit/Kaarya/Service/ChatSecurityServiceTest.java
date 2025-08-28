package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSecurityServiceTest {

    @Mock
    private JobApplicationRepo jobApplicationRepo;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChatSecurityService chatSecurityService;



    @Test
    void hasAccessToRoom_InvalidRoomId_ReturnsFalse() {
        // Act
        boolean result = chatSecurityService.hasAccessToRoom("invalid_room", authentication);

        // Assert
        assertFalse(result);
    }

    @Test
    void hasAccessToRoom_ApplicationNotFound_ReturnsFalse() {
        // Arrange
        String roomId = "chat_123";
        when(jobApplicationRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = chatSecurityService.hasAccessToRoom(roomId, authentication);

        // Assert
        assertFalse(result);
    }

    @Test
    void hasAccessToRoom_ExceptionThrown_ReturnsFalse() {
        // Arrange
        String roomId = "chat_123";
        when(jobApplicationRepo.findById(anyLong())).thenThrow(new RuntimeException("DB error"));

        // Act
        boolean result = chatSecurityService.hasAccessToRoom(roomId, authentication);

        // Assert
        assertFalse(result);
    }
}