package com.Ankit.Kaarya.Payloads;

import com.Ankit.Kaarya.Entity.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Data
public class IndustryDto {

    private Long industryId;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    @Column(name = "PHONE NO.")
    private String phoneNo;


    @NotNull(message = "Location is mandatory")
    private LocationDto location;


    private String role;

    @URL(message = "Invalid URL format")
    private String imageUrl;

    @JsonIgnore
    List<IndustryJobApplicationsDto> JobApplications;
}