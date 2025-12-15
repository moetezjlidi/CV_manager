package com.architecture.gestion_de_cvs.repository;

import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.security.XUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Person entities.
 */
public interface PersonRepository extends JpaRepository<Person, Long> {
    Page<Person> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
            String lastName, String firstName, Pageable pageable);

    Page<Person> findDistinctByActivities_TitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Check if a person with the given email already exists.
     * Used during signup to prevent duplicate email addresses.
     */
    boolean existsByEmail(String email);

    /**
     * Find a Person by their associated XUser.
     * Used to retrieve a user's CV.
     */
    Optional<Person> findByUser(XUser user);

    /**
     * Find a Person by their email address.
     */
    Optional<Person> findByEmail(String email);
}