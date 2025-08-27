package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Payloads.JobsDto;
import com.Ankit.Kaarya.Payloads.UsersDto;
import com.Ankit.Kaarya.Security.JwtAuthFilter;
import com.Ankit.Kaarya.Security.JwtUtil;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.JobService;
import com.Ankit.Kaarya.Service.RedisLocationService;
import com.Ankit.Kaarya.Service.UserLocationService;
import com.Ankit.Kaarya.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserLocationService userLocationService;
    private final RedisLocationService redisLocationService;
    private final JobService jobService;





    @PostMapping
    public ResponseEntity<UsersDto> registerUser(@RequestBody UsersDto usersDto) {
        // Call service to register user; assigns ROLE_USER as default role
        UsersDto createdUser = userService.registerUser(usersDto, "ROLE_USER");
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UsersDto> getUserProfile() {
        Long userId = getAuthenticatedUserId();
        UsersDto profile = userService.getProfile(Math.toIntExact(userId));
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }



    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UsersDto> updateUser(@RequestBody UsersDto usersDto) {
        Long userId = getAuthenticatedUserId();
        UsersDto updated = userService.updateUser(usersDto, userId.intValue());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @GetMapping("/applied-jobs")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<JobsDto>> getAppliedJobs() {
        Long userId = getAuthenticatedUserId();
        List<JobsDto> appliedJobs = userService.getAppliedJobsByUserId(userId.intValue());
        return new ResponseEntity<>(appliedJobs, HttpStatus.OK);
    }


    @PostMapping("/upload-image")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UsersDto> uploadUserImage(
            @RequestParam("image") MultipartFile file) throws IOException {
        Long userId = getAuthenticatedUserId();
        UsersDto updatedUser = userService.uploadUserImage(userId, file);
        return ResponseEntity.ok(updatedUser);
    }


    private Long getAuthenticatedUserId() {
        OtpAuthenticationToken authentication =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getId();
    }

    @PostMapping("/current-location")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> updateCurrentLocation(@RequestBody Location location) {
        Long userId = getAuthenticatedUserId();
        redisLocationService.saveUserLocation(userId, location);
        return ResponseEntity.ok("Location updated successfully");
    }


    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> filterJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam(required = false) Double radiusKm) {

        Long userId = getAuthenticatedUserId();
        Location currentLocation = redisLocationService.getUserLocation(userId);

        if (currentLocation == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please set your location first using /users/current-location endpoint");
        }

        List<JobsDto> filteredJobs = jobService.filterJobs(title, workDate, currentLocation, radiusKm);
        return ResponseEntity.ok(filteredJobs);
    }
}