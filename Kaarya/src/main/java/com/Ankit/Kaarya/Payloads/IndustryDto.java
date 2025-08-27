package com.Ankit.Kaarya.Payloads;

import com.Ankit.Kaarya.Entity.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class IndustryDto {

    private Long industryId;
    private String name;
    private String email;
    private String phoneNo;
    private LocationDto location;
    private String role;
    private String imageUrl;

    @JsonIgnore
    List<IndustryJobApplicationsDto> JobApplications;
}