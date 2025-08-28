package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.*;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.PaymentDto;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import com.Ankit.Kaarya.Repo.PaymentRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private IndustryRepo industryRepo;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private JobApplicationRepo jobApplicationRepo;

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void getTotalAmountEarnedByUserId_ReturnsAmount() {
        // Arrange
        Long userId = 1L;
        Payment payment1 = new Payment();
        payment1.setAmount(100.0);
        payment1.setStatus(PaymentStatus.PAID);

        Payment payment2 = new Payment();
        payment2.setAmount(200.0);
        payment2.setStatus(PaymentStatus.PAID);

        List<Payment> payments = Arrays.asList(payment1, payment2);

        when(paymentRepo.findByUserId(userId)).thenReturn(payments);

        // Act
        Double result = paymentService.getTotalAmountEarnedByUserId(userId);

        // Assert
        assertEquals(300.0, result);
        verify(paymentRepo).findByUserId(userId);
    }
}