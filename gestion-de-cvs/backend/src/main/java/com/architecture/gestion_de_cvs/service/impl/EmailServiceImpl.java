package com.architecture.gestion_de_cvs.service.impl;

import com.architecture.gestion_de_cvs.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
public void sendInvitationEmail(String email, String firstName, String lastName, String invitationToken) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("noreply@cv-platform.com");
        message.setSubject("Invitation à rejoindre la plateforme de CVs");

        String invitationLink = baseUrl + "/invitation/" + invitationToken;

        message.setText("Bonjour ... \n" + invitationLink);

        mailSender.send(message);
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Email sending failed, but we continue.");
    }
}


    @Override
    public void sendWelcomeEmail(String email, String firstName, String lastName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("noreply@cv-platform.com");
        message.setSubject("Bienvenue sur la plateforme de CVs");

        String text = "Bonjour " + firstName + " " + lastName + ",\n\n" +
                "Bienvenue ! Votre compte a été créé avec succès.\n\n" +
                "Vous pouvez maintenant vous connecter : " + baseUrl + "\n\n" +
                "Cordialement,\n" +
                "L'équipe CV Platform";

        message.setText(text);
        mailSender.send(message);
    }
}
