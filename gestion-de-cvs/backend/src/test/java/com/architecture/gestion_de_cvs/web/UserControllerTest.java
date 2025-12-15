package com.architecture.gestion_de_cvs.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.architecture.gestion_de_cvs.security.JwtUserService;
import com.architecture.gestion_de_cvs.dto.SignupWithInvitationDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserControllerTest {

    @Mock
    JwtUserService userService;

    @InjectMocks
    UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signupWithInvitation_shouldReturnToken() {
    SignupWithInvitationDTO dto = new SignupWithInvitationDTO("u","p","e@e.com","t");

        when(userService.signupWithInvitation(any())).thenReturn("jwt");

        String res = userController.signupWithInvitation(dto);
        assertEquals("jwt", res);
        verify(userService, times(1)).signupWithInvitation(dto);
    }
}
