package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Service.EmailService;
import com.Ankit.Kaarya.Service.OtpService;
import com.Ankit.Kaarya.Service.SmsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;
    private final SmsService smsService;


    @PostMapping("/send/user")
    public ResponseEntity<String> sendUserOtp(@RequestParam String phoneNo, HttpSession session) {
        String formatted = phoneNo.replace("+91", "").trim();
        String otp = otpService.generateOtp(formatted);
        smsService.sendOtpSms("+91" + formatted, otp);
        session.setAttribute("IDENTITY_KEY", formatted);
        return ResponseEntity.ok("OTP sent to phone.");
    }


    @PostMapping("/send/industry")
    public ResponseEntity<String> sendIndustryOtp(@RequestParam String email, HttpSession session) {
        String cleanEmail = email.trim().toLowerCase();
        String otp = otpService.generateOtp(cleanEmail);
        emailService.sendOtpEmail(cleanEmail, otp);
        session.setAttribute("IDENTITY_KEY", cleanEmail);
        return ResponseEntity.ok("OTP sent to email.");
    }
}