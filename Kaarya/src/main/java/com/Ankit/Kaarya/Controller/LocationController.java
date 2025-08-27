package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.LocationService;
import com.Ankit.Kaarya.Service.RedisLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {


    private final LocationService locationService;


    private final RedisLocationService redisLocationService;

    @GetMapping("/nearby/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getNearbyIndustriesByUser() {
        Long userId = ((OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getId();

        try {
            List<Industry> industries = locationService.getNearbyIndustriesByUserId(userId);
            return ResponseEntity.ok(industries);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please set your location first using /users/current-location endpoint");
        }
    }

    @GetMapping("/current")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getCurrentLocation() {
        Long userId = ((OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getId();
        Location location = redisLocationService.getUserLocation(userId);

        if (location == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Location not set");
        }

        return ResponseEntity.ok(location);
    }
}