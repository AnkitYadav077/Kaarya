package com.Ankit.Kaarya.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JobApplication_Id")
    private Long jobApplicationId;

    @Column(name = "APPLIED_DATE")
    private LocalDateTime appliedDate;

    @Column(name = "PAY_AMOUNT")
    private Double payAmount;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @ManyToOne
    private Jobs jobs;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @PrePersist
    public void setPayAmountFromJob() {
        if (this.jobs != null && this.payAmount == null) {
            this.payAmount = this.jobs.getPayAmount();
        }
    }

    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;
}