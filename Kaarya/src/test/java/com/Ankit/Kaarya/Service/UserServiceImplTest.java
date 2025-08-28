package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Payloads.UsersDto;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private JobApplicationRepo jobApplicationRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_Success() {
        // Arrange
        UsersDto usersDto = new UsersDto();
        Users user = new Users();
        Users savedUser = new Users();
        UsersDto resultDto = new UsersDto();

        when(modelMapper.map(usersDto, Users.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UsersDto.class)).thenReturn(resultDto);

        // Act
        UsersDto result = userService.registerUser(usersDto, "USER");

        // Assert
        assertNotNull(result);
        verify(userRepo).save(user);
    }

    @Test
    void getProfile_Exists_ReturnsUserDto() {
        // Arrange
        Integer userId = 1;
        Users user = new Users();
        UsersDto dto = new UsersDto();

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UsersDto.class)).thenReturn(dto);

        // Act
        UsersDto result = userService.getProfile(userId);

        // Assert
        assertNotNull(result);
        verify(userRepo).findById(userId);
    }

    @Test
    void getProfile_NotFound_ThrowsException() {
        // Arrange
        Integer userId = 1;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getProfile(userId);
        });
    }

    @Test
    void getAppliedJobsByUserId_ReturnsJobs() {
        // Arrange
        Integer userId = 1;
        JobApplication application = new JobApplication();
        Jobs job = new Jobs();
        application.setJobs(job);
        List<JobApplication> applications = Arrays.asList(application);

        when(jobApplicationRepo.findByUsers_UserId(userId)).thenReturn(applications);
        when(modelMapper.map(any(), any())).thenReturn(new JobsDto());

        // Act
        List<JobsDto> result = userService.getAppliedJobsByUserId(userId);

        // Assert
        assertEquals(1, result.size());
        verify(jobApplicationRepo).findByUsers_UserId(userId);
    }

    @Test
    void uploadUserImage_Success() throws IOException {
        // Arrange
        Long userId = 1L;
        Users user = new Users();
        UsersDto dto = new UsersDto();
        String imageUrl = "http://example.com/image.jpg";

        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));
        when(imageService.uploadImage(any())).thenReturn(imageUrl);
        when(userRepo.save(any())).thenReturn(user);
        when(modelMapper.map(user, UsersDto.class)).thenReturn(dto);

        // Act
        UsersDto result = userService.uploadUserImage(userId, multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(imageUrl, user.getImageUrl());
        verify(imageService).uploadImage(multipartFile);
    }

    @Test
    void getUserById_Exists_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        Users user = new Users();

        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));

        // Act
        Users result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        verify(userRepo).findById(anyInt());
    }
}