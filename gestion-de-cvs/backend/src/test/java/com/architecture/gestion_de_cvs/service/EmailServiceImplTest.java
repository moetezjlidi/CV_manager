package com.architecture.gestion_de_cvs.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.architecture.gestion_de_cvs.service.impl.EmailServiceImpl;

class EmailServiceImplTest {

    @Mock
    JavaMailSender mailSender;

    @InjectMocks
    EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendInvitationEmail_shouldCallMailSender() {
        // Given
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendInvitationEmail("to@example.com", "First", "Last", "token-123");

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendWelcomeEmail_shouldCallMailSender() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendWelcomeEmail("user@example.com", "F", "L");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
