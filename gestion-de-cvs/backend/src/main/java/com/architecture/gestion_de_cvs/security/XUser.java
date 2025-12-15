package com.architecture.gestion_de_cvs.security;

import com.architecture.gestion_de_cvs.model.Person;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a user for JWT authentication.
 * Explicit constructors are provided to avoid depending on Lombok generation.
 */
@Entity
@Table(name = "x_users")
public class XUser {

    @Id
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "x_user_roles", joinColumns = @JoinColumn(name = "user_name"))
    @Column(name = "role", nullable = false)
    private Set<String> roles = new HashSet<>();

    /**
     * Bidirectional OneToOne relationship with Person.
     * This allows navigating from a user to their CV/Person entity.
     * The relationship is owned by Person (mappedBy = "user").
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Person person;

    // No-arg constructor required by JPA
    public XUser() {
    }

    // Explicit all-args constructor (resolves the error)
    public XUser(String userName, String password, Set<String> roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles == null ? new HashSet<>() : roles;
    }

    // Getters and setters (explicit to avoid Lombok)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles == null ? new HashSet<>() : roles;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

}