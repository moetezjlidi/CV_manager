package com.architecture.gestion_de_cvs.dto;

/**
 * Lightweight DTO for listing persons without loading all activity details.
 * Used by the browse/list endpoint to improve performance.
 */
public record PersonSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        int activityCount
) {
}
