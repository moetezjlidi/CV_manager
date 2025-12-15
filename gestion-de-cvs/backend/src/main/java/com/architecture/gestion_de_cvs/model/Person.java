package com.architecture.gestion_de_cvs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.architecture.gestion_de_cvs.security.XUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Represents a person in the system.
 */
@Entity
@Table(name = "person")
@Data
public class Person {

    /** Unique identifier of the person. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Last name of the person (mandatory). */
    @NotBlank
    @Column(nullable = false)
    private String lastName;

    /** First name of the person (mandatory). */
    @NotBlank
    @Column(nullable = false)
    private String firstName;

    /** Unique email address of the person (mandatory). */
    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    /** Personal website of the person (optional). */
    private String website;

    /** Birth date of the person (optional). */
    private LocalDate birthDate;

    /**
     * User account associated with this person (OPTIONAL).
     * Un CV peut exister sans compte utilisateur (cooptation en attente).
     * Le CV devient modifiable uniquement quand un XUser est associé.
     * EAGER fetch pour permettre l'accès à getOwnerUsername() en dehors de
     * transaction.
     */
    @OneToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_name", nullable = true, unique = true)
    @JsonIgnore // Ne pas exposer XUser dans l'API JSON
    private XUser user;

    /**
     * List of activities associated with the person.
     * Uses a OneToMany relationship with cascade and orphan removal.
     */
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Activity> activities = new ArrayList<>();

    /**
     * Expose the username of the owner without exposing the full XUser entity.
     * This is a transient field computed from the user relationship.
     */
    @Transient
    public String getOwnerUsername() {
        return user != null ? user.getUserName() : null;
    }
}
