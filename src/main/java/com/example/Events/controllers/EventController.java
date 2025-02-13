package com.example.Events.controllers;

import com.example.Events.dtos.EventDto;
import com.example.Events.dtos.UpdateEventDto;
import com.example.Events.models.Event;
import com.example.Events.models.EventStatus;
import com.example.Events.models.UserEvent;
import com.example.Events.models.UserEventsFilter;
import com.example.Events.services.EventService;
import com.example.Events.services.UserEventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.Map;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final UserEventService userEventService;

    public EventController(EventService eventService, UserEventService userEventService) {
        this.eventService = eventService;
        this.userEventService = userEventService;
    }

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
    public ResponseEntity<Object> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            return ResponseEntity.ok(event);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
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

    @PostMapping("/{id}/signup")
    public ResponseEntity<Object> signUpForEvent(@PathVariable Long id) {
        try {
            UserEvent userEvent = userEventService.signUpForEvent(id);
            return ResponseEntity.ok(userEvent);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }
}
