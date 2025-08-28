package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLocationService {

    private final RedisTemplate<String, Location> locationRedisTemplate;
    private static final String LOCATION_KEY_PREFIX = "user:location:";

    public void saveUserLocation(Long userId, Location location) {
        String key = LOCATION_KEY_PREFIX + userId;
        locationRedisTemplate.opsForValue().set(key, location);
    }

    public Location getUserLocation(Long userId) {
        String key = LOCATION_KEY_PREFIX + userId;
        return locationRedisTemplate.opsForValue().get(key);
    }

    public void deleteUserLocation(Long userId) {
        String key = LOCATION_KEY_PREFIX + userId;
        locationRedisTemplate.delete(key);
    }
}
