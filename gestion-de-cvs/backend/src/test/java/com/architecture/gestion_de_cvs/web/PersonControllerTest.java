package com.architecture.gestion_de_cvs.web;

import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class PersonControllerUnitTest {
    private PersonService personService;
    private PersonController controller;
    private Principal principal;

    @BeforeEach
    void setup() {
        personService = Mockito.mock(PersonService.class);
        controller = new PersonController(personService);
        principal = () -> "aaa";
    }

    @Test
    void getById_found_returnsPerson() {
        Person p = new Person();
        p.setId(2L);
        Mockito.when(personService.getById(2L)).thenReturn(Optional.of(p));

        ResponseEntity<Person> response = controller.getById(2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(p, response.getBody());
    }

    @Test
    void getById_notFound_returns404() {
        Mockito.when(personService.getById(3L)).thenReturn(Optional.empty());
        ResponseEntity<Person> response = controller.getById(3L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAll_returnsPage() {
        Page<Person> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Mockito.when(personService.getAll(0, 10)).thenReturn(page);
        Page<Person> result = controller.getAll(0, 10);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void searchByName_returnsPage() {
        Page<Person> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Mockito.when(personService.searchByName("jo", 0, 10)).thenReturn(page);
        Page<Person> result = controller.searchByName("jo", 0, 10);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void searchByActivityTitle_returnsPage() {
        Page<Person> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Mockito.when(personService.searchByActivityTitle("eng", 0, 10)).thenReturn(page);
        Page<Person> result = controller.searchByActivityTitle("eng", 0, 10);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void createPerson_returnsCreated() {
        Person payload = new Person();
        payload.setFirstName("John");
        payload.setLastName("Doe");
        payload.setEmail("john@example.com");

        Person created = new Person();
        created.setId(1L);
        created.setFirstName(payload.getFirstName());
        created.setLastName(payload.getLastName());
        created.setEmail(payload.getEmail());

        Mockito.when(personService.create(any(Person.class))).thenReturn(created);

        ResponseEntity<Person> response = controller.create(payload, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void update_returnsPerson() {
        Person update = new Person();
        update.setFirstName("John");
        update.setLastName("Smith");
        update.setEmail("john@example.com");

        Person updated = new Person();
        updated.setId(1L);
        updated.setFirstName(update.getFirstName());
        updated.setLastName(update.getLastName());
        updated.setEmail(update.getEmail());

        Mockito.when(personService.getById(1L)).thenReturn(Optional.of(updated));
        Mockito.when(personService.canUserModifyPerson(anyString(), eq(updated))).thenReturn(true);
        Mockito.when(personService.update(eq(1L), any(Person.class))).thenReturn(updated);

        Person result = controller.update(1L, update, principal);
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void delete_returnsNoContent() {
        Person p = new Person();
        p.setId(1L);
        Mockito.when(personService.getById(1L)).thenReturn(Optional.of(p));
        Mockito.when(personService.canUserModifyPerson(anyString(), eq(p))).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(1L, principal);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(personService).delete(1L);
    }
}
