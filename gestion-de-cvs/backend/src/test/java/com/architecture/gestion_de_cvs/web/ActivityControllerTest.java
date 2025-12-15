package com.architecture.gestion_de_cvs.web;

import com.architecture.gestion_de_cvs.model.Activity;
import com.architecture.gestion_de_cvs.model.NatureActivity;
import com.architecture.gestion_de_cvs.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createActivity_returnsCreatedActivity() {
        Activity payload = new Activity();
        payload.setActivityYear(2024);
        payload.setNature(NatureActivity.TRAINING);
        payload.setTitle("Spring Boot Training");

        Activity created = new Activity();
        created.setId(10L);
        created.setActivityYear(payload.getActivityYear());
        created.setNature(payload.getNature());
        created.setTitle(payload.getTitle());

        Mockito.when(activityService.create(any(Activity.class))).thenReturn(created);
        java.security.Principal principal = Mockito.mock(java.security.Principal.class);
        Mockito.when(principal.getName()).thenReturn("user");

        var response = activityController.create(payload, principal);
        assertNotNull(response);
        assertEquals(10L, response.getBody().getId());
        assertEquals("Spring Boot Training", response.getBody().getTitle());
        assertEquals(org.springframework.http.HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getById_found_returnsActivity() {
        Activity a = new Activity();
        a.setId(11L);
        a.setActivityYear(2023);
        a.setNature(NatureActivity.PROJECTS);
        a.setTitle("University Project");
        Mockito.when(activityService.getById(11L)).thenReturn(Optional.of(a));

        var response = activityController.getById(11L);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("University Project", response.getBody().getTitle());
    }

    @Test
    void getById_notFound_returnsEmpty() {
        Mockito.when(activityService.getById(12L)).thenReturn(Optional.empty());
        var response = activityController.getById(12L);
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAll_returnsList() {
        Mockito.when(activityService.getAll()).thenReturn(List.of());
        List<Activity> result = activityController.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void update_returnsUpdatedActivity() {
        Activity update = new Activity();
        update.setActivityYear(2022);
        update.setNature(NatureActivity.EXPERIENCE_PROFESSIONAL);
        update.setTitle("Engineer");

        Activity updated = new Activity();
        updated.setId(13L);
        updated.setActivityYear(update.getActivityYear());
        updated.setNature(update.getNature());
        updated.setTitle(update.getTitle());

        Mockito.when(activityService.getById(13L)).thenReturn(Optional.of(updated));
        Mockito.when(activityService.update(eq(13L), any(Activity.class))).thenReturn(updated);
        java.security.Principal principal = Mockito.mock(java.security.Principal.class);
        Mockito.when(principal.getName()).thenReturn("user");

        Activity result = activityController.update(13L, update, principal);
        assertNotNull(result);
        assertEquals(13L, result.getId());
        assertEquals("Engineer", result.getTitle());
    }

    @Test
    void delete_callsService() {
        Activity existing = new Activity();
        existing.setId(14L);
        Mockito.when(activityService.getById(14L)).thenReturn(Optional.of(existing));
        java.security.Principal principal = Mockito.mock(java.security.Principal.class);
        Mockito.when(principal.getName()).thenReturn("user");
        var response = activityController.delete(14L, principal);
        assertEquals(org.springframework.http.HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(activityService).delete(14L);
    }

    @Test
    void searchByTitle_returnsList() {
        Mockito.when(activityService.searchByTitle("spring")).thenReturn(List.of());
        List<Activity> result = activityController.search("spring");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
