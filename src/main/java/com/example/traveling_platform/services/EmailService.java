package com.example.traveling_platform.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String email, String token) {
        String verificationLink = "http://localhost:8080/trusted/auth/verify-email?token=" + token;
        String subject = "Verify your email";
        String body = "Click the following link to verify your email: " + verificationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }
}

