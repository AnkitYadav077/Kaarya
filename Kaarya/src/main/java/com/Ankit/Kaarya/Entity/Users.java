package com.Ankit.Kaarya.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "USERS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long userId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CONTACT NO.")
    private String phoneNo;

    @Column(name = "UPI ID")
    private String upiId;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "IMAGE_URL", length = 500)
    private String imageUrl;

    @Embedded
    private Location location;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobApplication> jobs = new ArrayList<>();
}