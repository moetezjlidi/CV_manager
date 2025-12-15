package com.architecture.gestion_de_cvs.service;

import com.architecture.gestion_de_cvs.model.Activity;

import java.util.List;
import java.util.Optional;


public interface ActivityService {
    Activity create(Activity activity);

    Optional<Activity> getById(Long id);

    List<Activity> getAll();

    Activity update(Long id, Activity activity);

    void delete(Long id);

    List<Activity> searchByTitle(String title);
}
