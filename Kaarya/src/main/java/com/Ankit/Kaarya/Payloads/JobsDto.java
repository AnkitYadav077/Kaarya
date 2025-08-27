package com.Ankit.Kaarya.Payloads;

import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobsDto {

    private Long jobId;
    private String title;
    private int requiredWorkers;
    private String description;
    private Double payAmount;
    private LocalDateTime workDate;
    private LocalDateTime createdAt;


}