package com.architecture.gestion_de_cvs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


public record SignupDTO(

    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Password is required")
    String password,

    List<String> roles,

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Last name is required")
    String lastName,

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    String email,
    
    LocalDate birthDate,

    String website

) {
} 

