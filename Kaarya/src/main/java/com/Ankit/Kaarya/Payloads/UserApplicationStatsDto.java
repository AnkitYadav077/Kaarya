package com.Ankit.Kaarya.Payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserApplicationStatsDto {
    private long totalApplications;
    private long rejectedCount;
    private long approvedCount;

}