package com.Ankit.Kaarya.Payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJobApplicationSummary {
    private String jobTitle;
    private String industryName;
    private String status;
    private LocalDateTime appliedDate;
}