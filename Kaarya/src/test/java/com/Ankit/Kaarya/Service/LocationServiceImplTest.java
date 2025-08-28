package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Repo.LocationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepo locationRepo;

    @Mock
    private RedisLocationService redisLocationService;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void getNearbyIndustriesByUserId_Success() {
        // Arrange
        Long userId = 1L;
        Location location = new Location();
        Industry industry = new Industry();
        List<Industry> industries = Arrays.asList(industry);

        when(redisLocationService.getUserLocation(userId)).thenReturn(location);
        when(locationRepo.findNearbyIndustriesByLocation(any(Location.class), anyDouble()))
                .thenReturn(industries);

        // Act
        List<Industry> result = locationService.getNearbyIndustriesByUserId(userId);

        // Assert
        assertEquals(1, result.size());
        verify(redisLocationService).getUserLocation(userId);
    }

    @Test
    void getNearbyIndustriesByUserId_NoLocation_ThrowsException() {
        // Arrange
        Long userId = 1L;
        when(redisLocationService.getUserLocation(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            locationService.getNearbyIndustriesByUserId(userId);
        });
    }

    @Test
    void getNearbyIndustries_Success() {
        // Arrange
        Location location = new Location();
        Industry industry = new Industry();
        List<Industry> industries = Arrays.asList(industry);

        when(locationRepo.findNearbyIndustriesByLocation(any(Location.class), anyDouble()))
                .thenReturn(industries);

        // Act
        List<Industry> result = locationService.getNearbyIndustries(location, 10.0);

        // Assert
        assertEquals(1, result.size());
        verify(locationRepo).findNearbyIndustriesByLocation(any(Location.class), anyDouble());
    }
}