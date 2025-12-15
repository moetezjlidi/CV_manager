package com.architecture.gestion_de_cvs.repository;

import com.architecture.gestion_de_cvs.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.architecture.gestion_de_cvs.model.Activity;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import com.architecture.gestion_de_cvs.model.NatureActivity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PersonRepository.
 */
@DataJpaTest
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testCreateAndReadPerson() {
        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        person.setEmail("john.doe@example.com");
        Person savedPerson = personRepository.save(person);
        assertNotNull(savedPerson.getId());
        Optional<Person> retrievedPerson = personRepository.findById(savedPerson.getId());
        assertTrue(retrievedPerson.isPresent());
        assertEquals("Doe", retrievedPerson.get().getLastName());
    }

    @Test
    public void testUpdatePerson() {
        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        person.setEmail("john.doe@example.com");
        Person savedPerson = personRepository.save(person);
        savedPerson.setLastName("Smith");
        Person updatedPerson = personRepository.save(savedPerson);
        assertEquals("Smith", updatedPerson.getLastName());
    }

    @Test
    public void testDeletePerson() {
        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        person.setEmail("john.doe@example.com");
        Person savedPerson = personRepository.save(person);
        personRepository.delete(savedPerson);
        Optional<Person> retrievedPerson = personRepository.findById(savedPerson.getId());
        assertFalse(retrievedPerson.isPresent());
    }

    @Test
    void testFindByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase() {
        Person p1 = new Person();
        p1.setFirstName("Alice");
        p1.setLastName("Dupont");
        p1.setEmail("alice@example.com");
        personRepository.save(p1);

        Person p2 = new Person();
        p2.setFirstName("Bob");
        p2.setLastName("Martin");
        p2.setEmail("bob@example.com");
        personRepository.save(p2);

        Pageable pageable = PageRequest.of(0, 10);
        var page = personRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase("dup", "ali",
                pageable);

        assertThat(page.getContent()).extracting("firstName").contains("Alice");
        assertThat(page.getContent()).extracting("lastName").contains("Dupont");
    }

    @Test
    void testFindDistinctByActivities_TitleContainingIgnoreCase() {
        Person p1 = new Person();
        p1.setFirstName("Alice");
        p1.setLastName("Dupont");
        p1.setEmail("alice@example.com");

        Activity a1 = new Activity();
        a1.setTitle("Développement Java");
        a1.setActivityYear(2024);
        a1.setNature(NatureActivity.TRAINING);
        a1.setPerson(p1);

        p1.setActivities(List.of(a1));
        personRepository.save(p1);

        Person p2 = new Person();
        p2.setFirstName("Bob");
        p2.setLastName("Martin");
        p2.setEmail("bob@example.com");

        Activity a2 = new Activity();
        a2.setTitle("Gestion de projet");
        a2.setActivityYear(2024);
        a2.setNature(NatureActivity.TRAINING);
        a2.setPerson(p2);

        p2.setActivities(List.of(a2));
        personRepository.save(p2);

        Pageable pageable = PageRequest.of(0, 10);
        var page = personRepository.findDistinctByActivities_TitleContainingIgnoreCase("java", pageable);

        assertThat(page.getContent()).extracting("firstName").contains("Alice");
        assertThat(page.getContent()).extracting("lastName").contains("Dupont");
        assertThat(page.getContent()).doesNotContain(p2);
    }

    @Test
    void testExistsByEmail() {
        Person p = new Person();
        p.setFirstName("Test");
        p.setLastName("User");
        p.setEmail("testuser@example.com");
        personRepository.save(p);

        assertThat(personRepository.existsByEmail("testuser@example.com")).isTrue();
        assertThat(personRepository.existsByEmail("notfound@example.com")).isFalse();
    }
}