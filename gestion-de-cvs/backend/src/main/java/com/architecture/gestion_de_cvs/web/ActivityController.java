package com.architecture.gestion_de_cvs.web;

import com.architecture.gestion_de_cvs.model.Activity;
import com.architecture.gestion_de_cvs.model.NatureActivity;
import com.architecture.gestion_de_cvs.model.Person;
import com.architecture.gestion_de_cvs.repository.PersonRepository;
import com.architecture.gestion_de_cvs.service.ActivityService;
import com.architecture.gestion_de_cvs.service.PersonService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final PersonRepository personRepository;
    private final PersonService personService;

    public ActivityController(ActivityService activityService, PersonRepository personRepository,
            PersonService personService) {
        this.activityService = activityService;
        this.personRepository = personRepository;
        this.personService = personService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Activity> create(@Valid @RequestBody Activity activity, Principal principal) {
        if (activity.getPerson() != null && activity.getPerson().getId() != null) {
            Person person = personRepository.findById(activity.getPerson().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

            if (!personService.canUserModifyPerson(principal.getName(), person)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only add activities to your own CV");
            }
        }

        Activity created = activityService.create(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getById(@PathVariable Long id) {
        return activityService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Activity> getAll() {
        return activityService.getAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Activity update(@PathVariable Long id, @Valid @RequestBody Activity activity, Principal principal) {
        Activity existingActivity = activityService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        if (existingActivity.getPerson() != null) {
            if (!personService.canUserModifyPerson(principal.getName(), existingActivity.getPerson())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You can only modify activities in your own CV");
            }
        }

        return activityService.update(id, activity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        Activity existingActivity = activityService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found"));

        if (existingActivity.getPerson() != null) {
            if (!personService.canUserModifyPerson(principal.getName(), existingActivity.getPerson())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You can only delete activities from your own CV");
            }
        }

        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Activity> search(@RequestParam("title") String title) {
        return activityService.searchByTitle(title);
    }
}
