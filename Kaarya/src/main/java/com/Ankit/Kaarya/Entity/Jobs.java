package com.Ankit.Kaarya.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "JOBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Jobs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long jobId;


    @Column(name = "TITLE")
    private String title;


    @Column(name = "NO. OF WORKERS")
    private int requiredWorkers;



    @Column(name = "DESCRIPTION")
    private String description;


    @Column(name = "AMOUNT")
    private Double payAmount;


    @Column(name = "WORK DATE")
    private LocalDateTime workDate;

    @Column(name = "CREATED AT")
    private LocalDateTime createdAt;

    @ManyToOne
    private Industry industry;

    @OneToMany(mappedBy = "jobs", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobApplication> jobs = new ArrayList<>();
}