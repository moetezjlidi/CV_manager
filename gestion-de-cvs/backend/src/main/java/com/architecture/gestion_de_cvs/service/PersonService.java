package com.architecture.gestion_de_cvs.service;

import com.architecture.gestion_de_cvs.model.Person;
import org.springframework.data.domain.Page;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person create(Person person);

    Optional<Person> getById(Long id);

    Optional<Person> getByUsername(String username);

    Page<Person> getAll(int page, int size);

    Person update(Long id, Person person);

    void delete(Long id);

    Page<Person> searchByName(String query, int page, int size);

    Page<Person> searchByActivityTitle(String title, int page, int size);

    boolean canUserModifyPerson(String username, Person person);

    Person createCVForAuthenticatedUser(String username);

    String invitePersonForCooptation(String email, String firstName, String lastName,
            String website, LocalDate birthDate, String invitedBy);
}
