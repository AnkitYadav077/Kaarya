package com.Ankit.Kaarya.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "USERS", uniqueConstraints = {
        @UniqueConstraint(columnNames = "CONTACT NO.")
})
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


    @Column(name = "CONTACT NO.",unique = true)
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