package com.architecture.gestion_de_cvs.service;

import com.architecture.gestion_de_cvs.model.Activity;
import com.architecture.gestion_de_cvs.model.NatureActivity;
import com.architecture.gestion_de_cvs.repository.ActivityRepository;
import com.architecture.gestion_de_cvs.service.impl.ActivityServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ActivityServiceTest {
   @Test
    void createAndGet() {
    ActivityRepository repo = Mockito.mock(ActivityRepository.class);
    com.architecture.gestion_de_cvs.repository.PersonRepository personRepo = Mockito.mock(com.architecture.gestion_de_cvs.repository.PersonRepository.class);
    ActivityService service = new ActivityServiceImpl(repo, personRepo);
        Activity a = new Activity();
        a.setTitle("Software Engineer");
        a.setNature(NatureActivity.EXPERIENCE_PROFESSIONAL);
    a.setActivityYear(2023);

        Mockito.when(repo.save(any(Activity.class))).thenAnswer(inv -> {
            Activity val = inv.getArgument(0);
            val.setId(1L);
            return val;
        });
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(a));

        Activity created = service.create(a);
        assertNotNull(created.getId());
        assertEquals("Software Engineer", created.getTitle());

        Optional<Activity> found = service.getById(1L);
        assertTrue(found.isPresent());
    assertEquals(2023, found.get().getActivityYear());
    }

    @Test
    void searchByTitleUsesRepository() {
    ActivityRepository repo2 = Mockito.mock(ActivityRepository.class);
    com.architecture.gestion_de_cvs.repository.PersonRepository personRepo2 = Mockito.mock(com.architecture.gestion_de_cvs.repository.PersonRepository.class);
    ActivityService service2 = new ActivityServiceImpl(repo2, personRepo2);
    Mockito.when(repo2.findByTitleContainingIgnoreCase("engineer")).thenReturn(List.of());
    List<Activity> res = service2.searchByTitle("engineer");
    assertNotNull(res);
    }
}
