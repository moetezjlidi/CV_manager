package com.architecture.gestion_de_cvs.service.impl;

import com.architecture.gestion_de_cvs.model.Invitation;
import com.architecture.gestion_de_cvs.repository.InvitationRepository;
import com.architecture.gestion_de_cvs.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    public Invitation createInvitation(String email, String firstName, String lastName,
            String website, LocalDate birthDate, String invitedBy) {

        if (invitationRepository.existsByEmailAndUsedFalse(email)) {
            throw new IllegalStateException("Une invitation existe déjà pour cet email");
        }

        Invitation invitation = new Invitation();
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setEmail(email);
        invitation.setFirstName(firstName);
        invitation.setLastName(lastName);
        invitation.setWebsite(website);
        invitation.setBirthDate(birthDate);
        invitation.setInvitedBy(invitedBy);
        invitation.setUsed(false);

        return invitationRepository.save(invitation);
    }

    @Override
    public Invitation getValidInvitation(String token) {
        return invitationRepository.findValidInvitation(token, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Invitation invalide ou expirée"));
    }

    @Override
    public void markInvitationAsUsed(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invitation introuvable"));

        invitation.setUsed(true);
        invitation.setUsedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
    }

    @Override
    public boolean hasValidInvitation(String email) {
        return invitationRepository.existsByEmailAndUsedFalse(email);
    }
}
