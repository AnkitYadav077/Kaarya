package com.Ankit.Kaarya.Payloads;

import lombok.Data;

@Data
public class LocationDto {
    private double latitude;
    private double longitude;
    private Double radiusKm;
}