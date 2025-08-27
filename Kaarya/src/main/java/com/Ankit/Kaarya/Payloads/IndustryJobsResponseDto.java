package com.Ankit.Kaarya.Payloads;

import com.Ankit.Kaarya.Entity.Industry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndustryJobsResponseDto {
    private Industry industry;
    private List<JobsDto> jobs;
}