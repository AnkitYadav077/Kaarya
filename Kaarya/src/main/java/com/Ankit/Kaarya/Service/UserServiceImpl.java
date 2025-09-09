package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.JobApplication;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Payloads.UsersDto;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final JobApplicationRepo jobApplicationRepo;
    private final ModelMapper modelMapper;
    private final ImageService imageService;

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public UsersDto registerUser(UsersDto usersDto, String role) {
        // Check if phone number already exists
        if (userRepo.existsByPhoneNo(usersDto.getPhoneNo())) {
            throw new RuntimeException("Phone number already registered: " + usersDto.getPhoneNo());
        }

        Users user = dtoToUsers(usersDto);
        user.setRole(role);
        Users savedUser = userRepo.save(user);
        return userToDto(savedUser);
    }

    @Override
    @Cacheable(value = "user", key = "#userId")
    public UsersDto getProfile(Integer userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userToDto(user);
    }

    @Override
    @CacheEvict(value = {"user", "users"}, key = "#userId")
    public UsersDto updateUser(UsersDto usersDto, Integer userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setName(usersDto.getName());
        user.setUpiId(usersDto.getUpiId());

        Users updatedUser = userRepo.save(user);
        return userToDto(updatedUser);
    }

    @Override
    @Cacheable(value = "userAppliedJobs", key = "#userId")
    public List<JobsDto> getAppliedJobsByUserId(Integer userId) {
        List<JobApplication> applications = jobApplicationRepo.findByUsers_UserId(userId);
        return applications.stream()
                .map(app -> modelMapper.map(app.getJobs(), JobsDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"user", "users"}, key = "#userId")
    public UsersDto uploadUserImage(Long userId, MultipartFile file) throws IOException {
        Users user = userRepo.findById(userId.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String imageUrl = imageService.uploadImage(file);
        user.setImageUrl(imageUrl);
        Users updatedUser = userRepo.save(user);
        return userToDto(updatedUser);
    }

    @Override
    @Cacheable(value = "userById", key = "#userId")
    public Users getUserById(Long userId) {
        return userRepo.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private UsersDto userToDto(Users user) {
        return modelMapper.map(user, UsersDto.class);
    }

    private Users dtoToUsers(UsersDto usersDto) {
        return modelMapper.map(usersDto, Users.class);
    }
}