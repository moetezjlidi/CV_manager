package com.architecture.gestion_de_cvs.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.architecture.gestion_de_cvs.model.Invitation;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.repository.XUserRepository;
import com.architecture.gestion_de_cvs.service.EmailService;
import com.architecture.gestion_de_cvs.service.InvitationService;
import com.architecture.gestion_de_cvs.dto.SignupWithInvitationDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

class JwtUserServiceTest {

    @Mock
    XUserRepository userRepository;

    @Mock
    PersonRepository personRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    InvitationService invitationService;

    @Mock
    EmailService emailService;

    @InjectMocks
    JwtUserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signupWithInvitation_shouldThrowWhenEmailMismatch() {
    SignupWithInvitationDTO dto = new SignupWithInvitationDTO("u","p","wrong@example.com","token-123");

        Invitation inv = new Invitation();
        inv.setEmail("right@example.com");
        inv.setFirstName("Jean");
        inv.setLastName("Dupont");

        when(invitationService.getValidInvitation("token-123")).thenReturn(inv);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            userService.signupWithInvitation(dto);
        });
        assertTrue(ex.getMessage().toLowerCase().contains("email"));
    }

    @Test
    void signupWithInvitation_happyPath_createsUserAndPerson() {
    SignupWithInvitationDTO dto = new SignupWithInvitationDTO("newuser","pwd","inv@example.com","token-abc");

        Invitation inv = new Invitation();
        inv.setEmail("inv@example.com");
        inv.setFirstName("Marie");
        inv.setLastName("Curie");

        when(invitationService.getValidInvitation("token-abc")).thenReturn(inv);
        when(userRepository.existsById("newuser")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtProvider.createToken(any())).thenReturn("jwt-token");

        String token = userService.signupWithInvitation(dto);

        assertEquals("jwt-token", token);
        verify(userRepository, times(1)).save(any());
        verify(personRepository, times(1)).save(any());
        verify(invitationService, times(1)).markInvitationAsUsed("token-abc");
    }
}
