package com.architecture.gestion_de_cvs.repository;

import com.architecture.gestion_de_cvs.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing Activity entities.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTitleContainingIgnoreCase(String title);
}