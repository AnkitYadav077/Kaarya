package com.Ankit.Kaarya.Payloads;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDto {

    private Long jobApplicationId;
    private LocalDateTime appliedDate;
    private Double payAmount;
    private String status;
    private Long jobId;
    private Long userId;
}