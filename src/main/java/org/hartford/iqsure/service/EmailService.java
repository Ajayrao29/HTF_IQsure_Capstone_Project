package org.hartford.iqsure.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@iqsure.com}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset OTP — IQsure");
            helper.setText(buildOtpHtml(otp), true);
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendGenericEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildOtpHtml(String otp) {
        return """
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e1e4e8; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">
              <div style="background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%); padding: 32px; text-align: center;">
                <h1 style="color: white; margin: 0; font-size: 28px; letter-spacing: 1px;">IQsure</h1>
                <p style="color: #e0e7ff; margin: 8px 0 0; font-size: 16px;">Secure Your Future with Knowledge</p>
              </div>
              <div style="padding: 40px; text-align: center; background-color: #ffffff;">
                <h2 style="color: #1f2937; margin-bottom: 24px;">Password Reset Request</h2>
                <p style="font-size: 16px; color: #4b5563; line-height: 1.5;">You requested to reset your password. Use the following dynamic OTP to proceed:</p>
                <div style="font-size: 48px; font-weight: 800; letter-spacing: 12px; color: #4f46e5; margin: 32px 0; padding: 24px; background: #f3f4f6; border-radius: 12px; display: inline-block; border: 2px dashed #4f46e5;">
                  %s
                </div>
                <p style="color: #9ca3af; font-size: 14px; margin-top: 24px;">This OTP is valid for <strong>10 minutes</strong>.<br>If you did not request this, please secure your account immediately.</p>
              </div>
              <div style="background: #f9fafb; padding: 20px; text-align: center; font-size: 13px; color: #6b7280; border-top: 1px solid #e5e7eb;">
                © 2026 IQsure Gamified Insurance Platform • All rights reserved.
              </div>
            </div>
            """.formatted(otp);
    }
}
