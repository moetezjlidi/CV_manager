package com.architecture.gestion_de_cvs.service;

public interface EmailService {
    void sendInvitationEmail(String email, String firstName, String lastName, String invitationLink);

    void sendWelcomeEmail(String email, String firstName, String lastName);
}
