package com.Ankit.Kaarya.Payloads;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDto {
    private Long id;
    private String paymentId;
    private String orderId;
    private Double amount;
    private String currency;
    private String status;
    private Long industryId;
    private String industryName;
    private Long jobApplicationId;
    private LocalDateTime createdAt;


    private LocalDateTime paymentDate;

    private Long jobId;
    private String jobTitle;

}