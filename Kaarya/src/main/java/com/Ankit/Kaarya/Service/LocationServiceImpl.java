package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Repo.LocationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepo locationRepo;
    private final RedisLocationService redisLocationService;

    @Override
    @Cacheable(value = "nearbyIndustries", key = "#userId")
    public List<Industry> getNearbyIndustriesByUserId(Long userId) {
        Location location = redisLocationService.getUserLocation(userId);

        if (location == null) {
            throw new RuntimeException("User location not set. Please update location first.");
        }

        return locationRepo.findNearbyIndustriesByLocation(location, 10.0);
    }

    @Override
    @Cacheable(value = "nearbyIndustriesByLocation", key = "{#location, #radiusKm}")
    public List<Industry> getNearbyIndustries(Location location, double radiusKm) {
        return locationRepo.findNearbyIndustriesByLocation(location, radiusKm);
    }
}