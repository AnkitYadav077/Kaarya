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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final IndustryRepo industryRepository;
    private final PaymentRepo paymentRepository;
    private final JobApplicationRepo jobApplicationRepo;
    private final RazorpayClient razorpayClient;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"payments", "userPayments"}, allEntries = true)
    public PaymentDto createPaymentOrder(PaymentDto paymentRequestDTO) throws Exception {
        log.info("Creating payment order for IndustryId: {}", paymentRequestDTO.getIndustryId());

        Industry industry = industryRepository.findById(Math.toIntExact(paymentRequestDTO.getIndustryId()))
                .orElseThrow(() -> new ResourceNotFoundException("Industry", "id", paymentRequestDTO.getIndustryId()));

        double amount = paymentRequestDTO.getAmount();
        int totalAmountInPaise = (int) (amount * 100);

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", totalAmountInPaise);
            orderRequest.put("currency", paymentRequestDTO.getCurrency());
            orderRequest.put("receipt", "order_rcptid_" + industry.getIndustryId());
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id").toString();
            log.info("Order created successfully with Razorpay. Order ID: {}", orderId);

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setAmount(amount);
            payment.setCurrency(paymentRequestDTO.getCurrency());
            payment.setStatus(PaymentStatus.CREATED);
            payment.setIndustry(industry);

            if (paymentRequestDTO.getJobApplicationId() != null) {
                JobApplication application = jobApplicationRepo.findById(paymentRequestDTO.getJobApplicationId())
                        .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", paymentRequestDTO.getJobApplicationId()));
                payment.setJobApplication(application);
            }

            payment.setCreatedAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment entity saved with ID: {}", savedPayment.getId());

            return paymentToDto(savedPayment);

        } catch (RazorpayException e) {
            log.error("Error while creating order with Razorpay: {}", e.getMessage(), e);
            throw new Exception("Failed to create order with Razorpay", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"payments", "userPayments"}, allEntries = true)
    public PaymentDto updatePayment(String orderId, String paymentId, String status) {
        log.info("Updating payment. OrderId: {}, PaymentId: {}, NewStatus: {}", orderId, paymentId, status);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for OrderId: " + orderId));

        PaymentStatus paymentStatus = mapStatusStringToPaymentStatus(status);

        payment.setPaymentId(paymentId);
        payment.setStatus(paymentStatus);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment updated successfully. Payment ID: {}", updatedPayment.getId());

        // Update job application status if payment is completed
        if (paymentStatus == PaymentStatus.COMPLETED && updatedPayment.getJobApplication() != null) {
            JobApplication application = updatedPayment.getJobApplication();
            application.setStatus(ApplicationStatus.PAYMENT_DONE);
            jobApplicationRepo.save(application);
            log.info("Updated job application {} to PAYMENT_DONE status", application.getJobApplicationId());
        }

        return paymentToDto(updatedPayment);
    }

    @Override
    @Cacheable(value = "userPayments", key = "#userId")
    public List<PaymentDto> getPaymentHistoryByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(this::convertToHistoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "userEarnings", key = "#userId")
    public Double getTotalAmountEarnedByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    private PaymentDto paymentToDto(Payment payment) {
        PaymentDto paymentDto = modelMapper.map(payment, PaymentDto.class);
        if (payment.getIndustry() != null) {
            paymentDto.setIndustryId(payment.getIndustry().getIndustryId());
            paymentDto.setIndustryName(payment.getIndustry().getName());
        }
        if (payment.getJobApplication() != null) {
            paymentDto.setJobApplicationId(payment.getJobApplication().getJobApplicationId());
        }
        paymentDto.setStatus(payment.getStatus().toString());
        paymentDto.setId(payment.getId());
        return paymentDto;
    }

    private PaymentStatus mapStatusStringToPaymentStatus(String status) {
        if (status == null) return PaymentStatus.UNKNOWN;

        return switch (status.toUpperCase()) {
            case "CREATED" -> PaymentStatus.CREATED;
            case "PAID", "CAPTURED" -> PaymentStatus.PAID;
            case "COMPLETED" -> PaymentStatus.COMPLETED;
            case "FAILED" -> PaymentStatus.FAILED;
            default -> PaymentStatus.UNKNOWN;
        };
    }

    private PaymentDto convertToHistoryDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentId(String.valueOf(payment.getId()));
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setStatus(payment.getStatus().toString());
        dto.setPaymentDate(payment.getCreatedAt());

        if (payment.getJobApplication() != null && payment.getJobApplication().getJobs() != null) {
            Jobs job = payment.getJobApplication().getJobs();
            dto.setJobId(job.getJobId());
            dto.setJobTitle(job.getTitle());
        }

        if (payment.getIndustry() != null) {
            dto.setIndustryId(payment.getIndustry().getIndustryId());
            dto.setIndustryName(payment.getIndustry().getName());
        }

        if (payment.getJobApplication() != null) {
            dto.setJobApplicationId(payment.getJobApplication().getJobApplicationId());
        }

        return dto;
    }
}