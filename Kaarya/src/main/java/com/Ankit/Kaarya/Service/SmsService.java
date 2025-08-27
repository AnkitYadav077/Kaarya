package com.Ankit.Kaarya.Service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private final String FROM_PHONE = "+15044748353";

    public void sendOtpSms(String to, String otp) {
        if (!to.startsWith("+")) {
            to = "+91" + to;
        }

        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_PHONE),
                "Kaarya Verification Code\n\nOTP: " + otp +
                        "\n\nThis code will expire in 5 minutes.\n\n- Team Kaarya"
        ).create();
    }
}