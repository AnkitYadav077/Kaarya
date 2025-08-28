package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLocationServiceTest {

    @Mock
    private RedisTemplate<String, Location> locationRedisTemplate;

    @Mock
    private ValueOperations<String, Location> valueOperations;

    @InjectMocks
    private UserLocationService userLocationService;

    @Test
    void updateUserLocation_Success() {
        // Arrange
        Long userId = 1L;
        Location location = new Location();
        when(locationRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        userLocationService.updateUserLocation(userId, location);

        // Assert
        verify(valueOperations).set(anyString(), any(Location.class));
    }

    @Test
    void getUserLocation_Success() {
        // Arrange
        Long userId = 1L;
        Location location = new Location();
        when(locationRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(location);

        // Act
        Location result = userLocationService.getUserLocation(userId);

        // Assert
        assertNotNull(result);
        verify(valueOperations).get(anyString());
    }
}