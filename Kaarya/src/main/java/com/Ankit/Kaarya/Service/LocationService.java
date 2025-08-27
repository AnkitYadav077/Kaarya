package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Location;
import com.Ankit.Kaarya.Payloads.LocationDto;

import java.util.List;

public interface LocationService {
    List<Industry> getNearbyIndustriesByUserId(Long userId);
    public List<Industry> getNearbyIndustries(Location location, double radiusKm);


}