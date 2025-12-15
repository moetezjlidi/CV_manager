package com.architecture.gestion_de_cvs.service;

import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.repository.XUserRepository;
import com.architecture.gestion_de_cvs.service.impl.PersonServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PersonServiceTest {
    @Test
    void createAndGet() {
        PersonRepository repo = Mockito.mock(PersonRepository.class);
        XUserRepository xUserRepo = Mockito.mock(XUserRepository.class);
        InvitationService invitationService = Mockito.mock(InvitationService.class);
        EmailService emailService = Mockito.mock(EmailService.class);
        PersonService service = new PersonServiceImpl(repo, xUserRepo, invitationService, emailService);
        Person p = new Person();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setEmail("john@example.com");

        Mockito.when(repo.save(any(Person.class))).thenAnswer(inv -> {
            Person val = inv.getArgument(0);
            val.setId(1L);
            return val;
        });
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(p));

        Person created = service.create(p);
        assertNotNull(created.getId());
        assertEquals("John", created.getFirstName());

        Optional<Person> found = service.getById(1L);
        assertTrue(found.isPresent());
        assertEquals("Doe", found.get().getLastName());
    }

    @Test
    void searchByNameUsesRepository() {
        PersonRepository repo = Mockito.mock(PersonRepository.class);
    XUserRepository xUserRepo = Mockito.mock(XUserRepository.class);
    InvitationService invitationService = Mockito.mock(InvitationService.class);
    EmailService emailService = Mockito.mock(EmailService.class);
    PersonService service = new PersonServiceImpl(repo, xUserRepo, invitationService, emailService);

    Mockito.when(repo.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
        Mockito.eq("jo"), Mockito.eq("jo"), Mockito.any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    Page<Person> res = service.searchByName("jo", 0, 10);
    assertNotNull(res);
    }
}
