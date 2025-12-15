package com.architecture.gestion_de_cvs.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import com.architecture.gestion_de_cvs.model.Invitation;
import com.architecture.gestion_de_cvs.repository.InvitationRepository;
import com.architecture.gestion_de_cvs.service.impl.InvitationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InvitationServiceTest {

    @Mock
    InvitationRepository invitationRepository;

    @InjectMocks
    InvitationServiceImpl invitationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInvitation_shouldReturnInvitationWithToken() {
        when(invitationRepository.save(any(Invitation.class))).thenAnswer(i -> {
            Invitation inv = i.getArgument(0);
            inv.setToken("token-xyz");
            return inv;
        });

        Invitation inv = invitationService.createInvitation("a@b.com", "F", "L", "site", LocalDate.now(), "inviter");
        assertNotNull(inv);
        assertNotNull(inv.getToken());
    }

    @Test
    void getValidInvitation_invalid_shouldThrow() {
        when(invitationRepository.findByToken("nope")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> invitationService.getValidInvitation("nope"));
    }
}
