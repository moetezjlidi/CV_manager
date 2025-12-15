package com.architecture.gestion_de_cvs.dto;

import java.time.LocalDate;

public record InvitationResponse(
        String email,
        String firstName,
        String lastName,
        String website,
        LocalDate birthDate,
        String token) {
}