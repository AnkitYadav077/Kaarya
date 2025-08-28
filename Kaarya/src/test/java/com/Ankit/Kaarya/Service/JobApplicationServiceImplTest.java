package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.*;
import com.Ankit.Kaarya.Exceptions.InvalidStatusException;
import com.Ankit.Kaarya.Exceptions.PaymentProcessingException;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.JobApplicationDto;
import com.Ankit.Kaarya.Payloads.PaymentDto;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Repo.JobRepo;
import com.Ankit.Kaarya.Repo.PaymentRepo;
import com.Ankit.Kaarya.Repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepo jobApplicationRepo;

    @Mock
    private JobRepo jobRepo;

    @Mock
    private ChatService chatService;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private JobApplicationServiceImpl jobApplicationService;


    @Test
    void applyForJob_AlreadyApplied_ThrowsException() {
        // Arrange
        Long jobId = 1L;
        Long userId = 1L;
        when(jobApplicationRepo.existsByUsersUserIdAndStatusIn(any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            jobApplicationService.applyForJob(jobId, userId);
        });
    }


    @Test
    void completeApplication_InvalidStatus_ThrowsException() {
        // Arrange
        Long applicationId = 1L;
        JobApplication application = new JobApplication();
        application.setStatus(ApplicationStatus.PENDING);

        when(jobApplicationRepo.findById(applicationId)).thenReturn(Optional.of(application));

        // Act & Assert
        assertThrows(InvalidStatusException.class, () -> {
            jobApplicationService.completeApplication(applicationId);
        });
    }



    @Test
    void initiatePayment_InvalidStatus_ThrowsException() {
        // Arrange
        Long applicationId = 1L;
        JobApplication application = new JobApplication();
        application.setStatus(ApplicationStatus.PENDING);

        when(jobApplicationRepo.findById(applicationId)).thenReturn(Optional.of(application));

        // Act & Assert
        assertThrows(InvalidStatusException.class, () -> {
            jobApplicationService.initiatePayment(applicationId);
        });
    }

    @Test
    void getCompletedApplicationsCountByUserId_ReturnsCount() {
        // Arrange
        Long userId = 1L;
        when(jobApplicationRepo.countByUsers_UserIdAndStatusIn(anyInt(), any()))
                .thenReturn(5L);

        // Act
        long result = jobApplicationService.getCompletedApplicationsCountByUserId(userId);

        // Assert
        assertEquals(5, result);
    }
}