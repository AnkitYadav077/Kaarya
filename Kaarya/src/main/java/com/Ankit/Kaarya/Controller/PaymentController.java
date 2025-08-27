package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Payloads.PaymentDto;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.PaymentService;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    private Long getAuthenticatedUserId() {
        OtpAuthenticationToken auth =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getId();
    }

    @PostMapping("/create-order")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<?> createPaymentOrder(@RequestBody PaymentDto paymentDto) {
        try {
            PaymentDto createdOrder = paymentService.createPaymentOrder(paymentDto);
            return ResponseEntity.ok(createdOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Industry not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PutMapping("/update-payment")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<?> updatePayment(
            @RequestParam("orderId") String orderId,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("status") String status) {
        try {
            PaymentDto updatedPayment = paymentService.updatePayment(orderId, paymentId, status);
            return ResponseEntity.ok(updatedPayment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Payment not found: " + orderId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/history/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getPaymentHistoryByUserId() {
        try {
            Long userId = getAuthenticatedUserId();
            List<PaymentDto> history = paymentService.getPaymentHistoryByUserId(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching payment history: " + e.getMessage());
        }
    }

    @GetMapping("/total-earned/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getTotalAmountEarnedByUserId() {
        try {
            Long userId = getAuthenticatedUserId();
            Double totalAmount = paymentService.getTotalAmountEarnedByUserId(userId);
            return ResponseEntity.ok(totalAmount);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error calculating total amount: " + e.getMessage());
        }
    }
}