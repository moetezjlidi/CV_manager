package com.architecture.gestion_de_cvs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record InvitationRequest(
        @NotBlank(message = "Le prénom est obligatoire") @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères") String firstName,

        @NotBlank(message = "Le nom est obligatoire") @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères") String lastName,

        @NotBlank(message = "L'email est obligatoire") @Email(message = "Format d'email invalide") String email,

        String website,

        LocalDate birthDate) {
}