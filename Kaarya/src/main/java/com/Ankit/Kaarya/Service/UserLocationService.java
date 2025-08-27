package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final RedisTemplate<String, Location> locationRedisTemplate;
    private static final String KEY_PREFIX = "user:location:";

    public void updateUserLocation(Long userId, Location location) {
        String key = KEY_PREFIX + userId;
        locationRedisTemplate.opsForValue().set(key, location);
    }

    public Location getUserLocation(Long userId) {
        String key = KEY_PREFIX + userId;
        return locationRedisTemplate.opsForValue().get(key);
    }
}