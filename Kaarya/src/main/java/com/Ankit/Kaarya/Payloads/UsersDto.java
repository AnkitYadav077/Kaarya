package com.Ankit.Kaarya.Payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {

    private Long userId;
    private String name;
    private String phoneNo;
    private String upiId;
    private LocationDto location;
    private String role;
    private String imageUrl;
}