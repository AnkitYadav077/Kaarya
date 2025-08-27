package com.Ankit.Kaarya.Payloads;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantWithTeamSizeDto {
    private Long userId;
    private String name;
    private String phoneNo;
    private String upiId;
    private String status;
}