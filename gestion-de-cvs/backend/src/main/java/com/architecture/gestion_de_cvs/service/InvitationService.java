package com.architecture.gestion_de_cvs.service;

import com.architecture.gestion_de_cvs.model.Invitation;
import java.time.LocalDate;

public interface InvitationService {
    Invitation createInvitation(String email, String firstName, String lastName,
            String website, LocalDate birthDate, String invitedBy);

    Invitation getValidInvitation(String token);

    void markInvitationAsUsed(String token);

    boolean hasValidInvitation(String email);
}
