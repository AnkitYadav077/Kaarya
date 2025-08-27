package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Payloads.UsersDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {


    UsersDto registerUser(UsersDto users,String role);

    UsersDto getProfile(Integer userId);

    UsersDto updateUser(UsersDto users,Integer userId);

    List<JobsDto> getAppliedJobsByUserId(Integer userId);

    UsersDto uploadUserImage(Long userId, MultipartFile file) throws IOException;
    Users getUserById(Long userId);

}