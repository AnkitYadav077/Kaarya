package com.Ankit.Kaarya.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Your One-Time Password (OTP)");
            helper.setText(buildEmailHtml(otp), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send OTP email", e);
        }
    }

    private String buildEmailHtml(String otp) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>OTP Verification</title>" +
                "    <style>" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f7fa; padding: 20px; }" +
                "        .email-container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); }" +
                "        .header { background: linear-gradient(135deg, #4776E6 0%, #8E54E9 100%); padding: 40px 20px; text-align: center; position: relative; overflow: hidden; }" +
                "        .header::before { content: ''; position: absolute; top: -50%; left: -50%; width: 200%; height: 200%; background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 60%); transform: rotate(30deg); }" +
                "        .header h1 { color: white; font-size: 32px; font-weight: 700; margin: 0; position: relative; letter-spacing: 0.5px; }" +
                "        .content { padding: 40px 30px; text-align: center; color: #333; }" +
                "        .message { font-size: 16px; line-height: 1.7; max-width: 85%; margin: 0 auto 25px; color: #555; }" +
                "        .otp-section { position: relative; margin: 30px 0; }" +
                "        .otp-box { display: inline-block; background: #f7f9ff; padding: 15px 30px; border-radius: 12px; margin: 15px 0; font-size: 42px; font-weight: 700; letter-spacing: 8px; color: #4776E6; border: 2px dashed #8E54E9; }" +
                "        .timer { font-size: 18px; font-weight: 600; color: #E91E63; margin: 10px 0; }" +
                "        .warning { background: #fff8e6; border-left: 4px solid #ffc107; padding: 15px; margin: 30px auto; max-width: 85%; text-align: left; border-radius: 0 8px 8px 0; }" +
                "        .support-link { color: #4776E6; text-decoration: none; font-weight: 600; }" +
                "        .footer { background: #f8f9fc; padding: 25px; text-align: center; color: #6c767e; font-size: 13px; border-top: 1px solid #eee; }" +
                "        .logo { color: #4776E6; font-weight: 700; font-size: 18px; letter-spacing: 1px; margin-bottom: 10px; }" +
                "        @media (max-width: 480px) { " +
                "            .otp-box { font-size: 32px; letter-spacing: 5px; padding: 12px 20px; }" +
                "            .header h1 { font-size: 26px; }" +
                "            .message { max-width: 100%; }" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"email-container\">" +
                "        <div class=\"header\">" +
                "            <h1>Secure Verification Code</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p class=\"message\">Your one-time password (OTP) for account verification is:</p>" +
                "            <div class=\"otp-section\">" +
                "                <div class=\"otp-box\">" + otp + "</div>" +
                "                <div class=\"timer\">⏳ This code will expire in <strong>5 minutes</strong></div>" +
                "            </div>" +
                "            <div class=\"warning\">" +
                "                <p>⚠️ <strong>Security notice:</strong> Kaarya will never ask for this code via phone call or email.</p>" +
                "                <p>Please use this OTP immediately as it expires quickly for security reasons.</p>" +
                "            </div>" +
                "            <p>Need help? <a href=\"mailto:support@kaarya.com\" class=\"support-link\">Contact our support team</a></p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <div class=\"logo\">KAARYA</div>" +
                "            <p>© 2023 Kaarya. All rights reserved.<br>" +
                "            This is an automated message - please do not reply</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private static class EmailSendingException extends RuntimeException {
        public EmailSendingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}