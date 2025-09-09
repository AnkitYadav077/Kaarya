package com.Ankit.Kaarya.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing the Industry table.
 */
@Entity
@Table(name = "INDUSTRY", uniqueConstraints = {
        @UniqueConstraint(columnNames = "EMAIL")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long industryId;


    @Column(name = "NAME")
    private String name;


    @Column(name = "EMAIL",unique = true)
    private String email;


    @Column(name = "PHONE NO.")
    private String phoneNo;

    @Column(name = "ROLE")
    private String role;


    @Column(name = "IMAGE_URL", length = 500)
    private String imageUrl;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "longitude"))
    })
    private Location location;

    @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Jobs> jobs = new ArrayList<>();
}