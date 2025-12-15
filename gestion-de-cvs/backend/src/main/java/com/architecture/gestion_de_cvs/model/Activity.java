package com.architecture.gestion_de_cvs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents an activity in a CV.
 */
@Entity
@Table(name = "activity", indexes = {
        @Index(name = "idx_activity_title", columnList = "title")
})
@Data
public class Activity {

    /** Unique identifier of the activity. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Year of the activity (mandatory). */
    @NotNull
    @Column(name = "activity_year", nullable = false)
    private Integer activityYear;

    /** Nature of the activity (mandatory). */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private NatureActivity nature;

    /** Title of the activity (mandatory). */
    @NotBlank
    @Column(nullable = false)
    private String title;

    /** Description of the activity (optional). */
    private String description;

    /** Web address associated with the activity (optional). */
    private String web;

    /**
     * Person associated with the activity (mandatory relationship).
     * Uses a ManyToOne relationship with lazy loading.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference
    private Person person;
}
