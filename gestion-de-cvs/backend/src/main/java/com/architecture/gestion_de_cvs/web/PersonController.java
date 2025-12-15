package com.architecture.gestion_de_cvs.web;

import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.service.PersonService;
import com.architecture.gestion_de_cvs.dto.InvitationRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/send-invitation")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> inviteForCooptation(@Valid @RequestBody InvitationRequest request,
            Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be authenticated to send invitations");
        }

        personService.invitePersonForCooptation(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.website(),
                request.birthDate(),
                principal.getName());

        return ResponseEntity.ok("Invitation envoyée avec succès à " + request.email());
    }

    @PostMapping("/create-my-cv")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Person> createMyCV(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be authenticated");
        }

        Person created = personService.createCVForAuthenticatedUser(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/search")
    public Page<Person> searchByName(
            @RequestParam("q") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return personService.searchByName(q, page, size);
    }

    @GetMapping("/search-by-activity")
    public Page<Person> searchByActivityTitle(
            @RequestParam("title") String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return personService.searchByActivityTitle(title, page, size);
    }

    @GetMapping
    public Page<Person> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return personService.getAll(page, size);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Person> getMe(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be authenticated");
        }
        return personService.getByUsername(principal.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Créer un CV via cooptation (sans authentification)
    @PostMapping
    public ResponseEntity<Person> create(@Valid @RequestBody Person person, Principal principal) {
        if (principal != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Authenticated users cannot create additional CVs. Your CV was created at signup. Use PUT to update it.");
        }

        Person created = personService.create(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable Long id) {
        return personService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Person update(@PathVariable Long id, @Valid @RequestBody Person person, Principal principal) {
        Person existingPerson = getPersonAndCheckOwnership(id, principal);
        return personService.update(id, person);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        getPersonAndCheckOwnership(id, principal);
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Person getPersonAndCheckOwnership(Long id, Principal principal) {
        Person person = personService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        if (!personService.canUserModifyPerson(principal.getName(), person)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own CV");
        }

        return person;
    }
}
