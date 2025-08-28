package com.Ankit.Kaarya.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OtpService otpService;

    @Test
    void generateOtp_Email_Success() {
        // Arrange
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        String otp = otpService.generateOtp(email);

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void generateOtp_Phone_Success() {
        // Arrange
        String phone = "1234567890";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        String otp = otpService.generateOtp(phone);

        // Assert
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void validateOtp_ValidOtp_ReturnsTrue() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(otp);

        // Act
        boolean isValid = otpService.validateOtp(email, otp);

        // Assert
        assertTrue(isValid);
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void validateOtp_InvalidOtp_ReturnsFalse() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("654321");

        // Act
        boolean isValid = otpService.validateOtp(email, otp);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateOtp_NoOtp_ReturnsFalse() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        // Act
        boolean isValid = otpService.validateOtp(email, otp);

        // Assert
        assertFalse(isValid);
    }
}