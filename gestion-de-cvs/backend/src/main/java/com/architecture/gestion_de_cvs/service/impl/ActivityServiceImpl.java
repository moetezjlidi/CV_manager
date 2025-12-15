package com.architecture.gestion_de_cvs.service.impl;

import com.architecture.gestion_de_cvs.exception.ResourceNotFoundException;
import com.architecture.gestion_de_cvs.model.Activity;
import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.repository.ActivityRepository;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final PersonRepository personRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, PersonRepository personRepository) {
        this.activityRepository = activityRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public Activity create(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Activity> getById(Long id) {
        return activityRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> getAll() {
        return activityRepository.findAll();
    }

    @Override
    @Transactional
    public Activity update(Long id, Activity activity) {
        return activityRepository.findById(id)
                .map(existing -> {
                    existing.setActivityYear(activity.getActivityYear());
                    existing.setNature(activity.getNature());
                    existing.setTitle(activity.getTitle());
                    existing.setDescription(activity.getDescription());
                    existing.setWeb(activity.getWeb());
                    existing.setPerson(activity.getPerson());
                    return activityRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));

        Person person = activity.getPerson();
        if (person != null) {
            person.getActivities().remove(activity);
            personRepository.save(person);
        }

        activityRepository.delete(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> searchByTitle(String title) {
        return activityRepository.findByTitleContainingIgnoreCase(title);
    }
}
