package com.Ankit.Kaarya.Payloads;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobsDto {

    private Long jobId;

    @NotBlank(message = "Title is mandatory")
    @Size(min = 3, max = 20, message = "Title must be between 3 and 20 characters")
    private String title;

    @Min(value = 1, message = "At least 1 worker required")
    private int requiredWorkers;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 5, max = 40, message = "Description must be between 5 and 40 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private Double payAmount;

    @Future(message = "Work date must be in the future")
    private LocalDateTime workDate;

    private LocalDateTime createdAt;


}