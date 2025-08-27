package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Payloads.PaymentDto;

import java.util.List;

public interface PaymentService {
    PaymentDto createPaymentOrder(PaymentDto paymentDTO) throws Exception;
    PaymentDto updatePayment(String orderId, String paymentId, String status) throws Exception;


    List<PaymentDto> getPaymentHistoryByUserId(Long userId);

    Double getTotalAmountEarnedByUserId(Long userId);

}