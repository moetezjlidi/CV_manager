package com.architecture.gestion_de_cvs.web;

import com.architecture.gestion_de_cvs.model.Invitation;
import com.architecture.gestion_de_cvs.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.architecture.gestion_de_cvs.dto.InvitationResponse;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<InvitationResponse> getInvitation(@PathVariable String token) {
        Invitation invitation = invitationService.getValidInvitation(token);

        InvitationResponse invitationResponse = new InvitationResponse(
                invitation.getEmail(),
                invitation.getFirstName(),
                invitation.getLastName(),
                invitation.getWebsite(),
                invitation.getBirthDate(),
                invitation.getToken());

        return ResponseEntity.ok(invitationResponse);
    }
}
