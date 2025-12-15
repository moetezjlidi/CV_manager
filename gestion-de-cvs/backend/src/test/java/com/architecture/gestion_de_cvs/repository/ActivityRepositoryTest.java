package com.architecture.gestion_de_cvs.repository;

import com.architecture.gestion_de_cvs.model.Activity;
import com.architecture.gestion_de_cvs.model.NatureActivity;
import com.architecture.gestion_de_cvs.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ActivityRepository.
 */
@DataJpaTest
public class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testCreateAndReadActivity() {

        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        person.setEmail("john.doe@example.com");
        Person savedPerson = personRepository.save(person);

        Activity activity = new Activity();
        activity.setActivityYear(2025);
        activity.setNature(NatureActivity.EXPERIENCE_PROFESSIONAL);
        activity.setTitle("Software Engineer");
        activity.setDescription("Developed a Spring Boot application.");
        activity.setWeb("https://example.com");
        activity.setPerson(savedPerson);

        Activity savedActivity = activityRepository.save(activity);
        assertNotNull(savedActivity.getId());

        Optional<Activity> retrievedActivity = activityRepository.findById(savedActivity.getId());
        assertTrue(retrievedActivity.isPresent());
        assertEquals("Software Engineer", retrievedActivity.get().getTitle());
    }

    @Test
    public void testUpdateActivity() {

        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        person.setEmail("john.doe@example.com");
        Person savedPerson = personRepository.save(person);

        Activity activity = new Activity();
        activity.setActivityYear(2025);
        activity.setNature(NatureActivity.EXPERIENCE_PROFESSIONAL);
        activity.setTitle("Software Engineer");
        activity.setDescription("Developed a Spring Boot application.");
        activity.setWeb("https://example.com");
        activity.setPerson(savedPerson);
        Activity savedActivity = activityRepository.save(activity);

        savedActivity.setTitle("Senior Software Engineer");
        Activity updatedActivity = activityRepository.save(savedActivity);

        assertEquals("Senior Software Engineer", updatedActivity.getTitle());
    }

    @Test
    public void testDeleteActivity() {

        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        person.setEmail("john.doe@example.com");
        Person savedPerson = personRepository.save(person);

        Activity activity = new Activity();
        activity.setActivityYear(2025);
        activity.setNature(NatureActivity.EXPERIENCE_PROFESSIONAL);
        activity.setTitle("Software Engineer");
        activity.setDescription("Developed a Spring Boot application.");
        activity.setWeb("https://example.com");
        activity.setPerson(savedPerson);
        Activity savedActivity = activityRepository.save(activity);

        activityRepository.delete(savedActivity);

        Optional<Activity> retrievedActivity = activityRepository.findById(savedActivity.getId());
        assertFalse(retrievedActivity.isPresent());
    }
}