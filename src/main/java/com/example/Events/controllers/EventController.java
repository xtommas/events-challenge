package com.example.Events.controllers;

import com.example.Events.dtos.EventDto;
import com.example.Events.dtos.UpdateEventDto;
import com.example.Events.models.Event;
import com.example.Events.models.EventStatus;
import com.example.Events.services.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

//    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #statusParam != 'DRAFT')")
    @GetMapping
    public Page<Event> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate dateStart,
            @RequestParam(required = false) LocalDate dateEnd,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) EventStatus status
    ) {
        return eventService.getAllEvents(page, size, dateStart, dateEnd, title, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventDto eventDto) {
        Event event = eventService.createEvent(eventDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody UpdateEventDto dto) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
