package com.architecture.gestion_de_cvs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupWithInvitationDTO(

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,
    
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Invitation token is required")
    String invitationToken

) {
} 
