package com.Ankit.Kaarya.Payloads;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndustryJobApplicationsDto {
    private String jobTitle;
    private String description;
    private double paymentAmount;
    private int requiredWorkers;
    private List<ApplicantWithTeamSizeDto> applicants;
}