package com.Ankit.Kaarya.Service;

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
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        String redisKey = buildRedisKey(key);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(redisKey, otp, EXPIRATION_MINUTES, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOtp(String key, String otp) {
        String redisKey = buildRedisKey(key);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedOtp = ops.get(redisKey);

        if (storedOtp == null) {
            return false;
        }

        boolean isValid = storedOtp.equals(otp);
        if (isValid) {
            redisTemplate.delete(redisKey);
        }
        return isValid;
    }
}