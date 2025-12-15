package com.architecture.gestion_de_cvs.service.impl;

import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.repository.XUserRepository;
import com.architecture.gestion_de_cvs.security.XUser;
import com.architecture.gestion_de_cvs.service.PersonService;
import com.architecture.gestion_de_cvs.service.InvitationService;
import com.architecture.gestion_de_cvs.service.EmailService;
import com.architecture.gestion_de_cvs.model.Invitation;
import com.architecture.gestion_de_cvs.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final XUserRepository xUserRepository;
    private final InvitationService invitationService;
    private final EmailService emailService;

    public PersonServiceImpl(PersonRepository personRepository, XUserRepository xUserRepository,
            InvitationService invitationService, EmailService emailService) {
        this.personRepository = personRepository;
        this.xUserRepository = xUserRepository;
        this.invitationService = invitationService;
        this.emailService = emailService;
    }

    @Override
    public Person create(Person person) {
        return personRepository.save(person);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> getById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Person> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        return personRepository.findAll(pageable);
    }

    @Override
    public Person update(Long id, Person person) {
        return personRepository.findById(id)
                .map(existing -> {
                    existing.setLastName(person.getLastName());
                    existing.setFirstName(person.getFirstName());
                    existing.setEmail(person.getEmail());
                    existing.setWebsite(person.getWebsite());
                    existing.setBirthDate(person.getBirthDate());
                    return personRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found: " + id);
        }
        personRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Person> searchByName(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        return personRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query, query,
                pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Person> searchByActivityTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        return personRepository.findDistinctByActivities_TitleContainingIgnoreCase(title, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserModifyPerson(String username, Person person) {
        if (username == null || person == null || person.getUser() == null) {
            return false;
        }
        return username.equals(person.getUser().getUserName());
    }

    @Override
    public Person createCVForAuthenticatedUser(String username) {
        XUser user = xUserRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Optional<Person> existingPerson = personRepository.findByUser(user);
        if (existingPerson.isPresent()) {
            throw new IllegalStateException("User already has a CV");
        }

        Person newPerson = new Person();
        newPerson.setFirstName("Prénom");
        newPerson.setLastName("Nom");
        newPerson.setEmail(username + "@example.com");
        newPerson.setUser(user);

        return personRepository.save(newPerson);
    }

    @Override
    public String invitePersonForCooptation(String email, String firstName, String lastName,
            String website, LocalDate birthDate, String invitedBy) {
        XUser inviter = xUserRepository.findById(invitedBy)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + invitedBy));

        if (personRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Un CV existe déjà avec cet email");
        }

        Invitation invitation = invitationService.createInvitation(
                email, firstName, lastName, website, birthDate, invitedBy);

        emailService.sendInvitationEmail(email, firstName, lastName, invitation.getToken());

        return invitation.getToken();
    }

    @Override
    public Optional<Person> getByUsername(String username) {
        return xUserRepository.findById(username)
                .flatMap(personRepository::findByUser);
    }
}
