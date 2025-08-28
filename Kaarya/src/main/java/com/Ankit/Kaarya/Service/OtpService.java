package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Exceptions.OtpValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final long EXPIRATION_MINUTES = 5;
    private final StringRedisTemplate redisTemplate;

    private String buildRedisKey(String key) {
        if (key.contains("@")) {
            return "EMAIL_" + key;
        } else {
            return "PHONE_" + key;
        }
    }

    public String generateOtp(String key) {
        try {
            String otp = String.valueOf(new Random().nextInt(900000) + 100000);
            String redisKey = buildRedisKey(key);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            ops.set(redisKey, otp, EXPIRATION_MINUTES, TimeUnit.MINUTES);
            return otp;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate OTP: " + e.getMessage(), e);
        }
    }

    public boolean validateOtp(String key, String otp) {
        try {
            String redisKey = buildRedisKey(key);
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            String storedOtp = ops.get(redisKey);

            if (storedOtp == null) {
                throw new OtpValidationException("OTP has expired or doesn't exist");
            }

            boolean isValid = storedOtp.equals(otp);
            if (isValid) {
                redisTemplate.delete(redisKey);
                return true;
            } else {
                throw new OtpValidationException("Invalid OTP");
            }
        } catch (OtpValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new OtpValidationException("Error validating OTP: " + e.getMessage());
        }
    }
}