package com.example.Events.services;

import com.example.Events.dtos.EventDto;
import com.example.Events.dtos.UpdateEventDto;
import com.example.Events.models.Event;
import com.example.Events.models.EventStatus;
import com.example.Events.repositories.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Page<Event> getAllEvents(
            int page,
            int size,
            LocalDate dateStart,
            LocalDate dateEnd,
            String title,
            EventStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // Basically, an empty WHERE clause container.
        // We'll add conditions to it based on provided filters
        Specification<Event> spec = Specification.where(null);

        if (dateStart != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dateAndTime"), dateStart.atStartOfDay()));
        }

        if (dateEnd != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dateAndTime"), dateEnd.atTime(LocalTime.MAX)));
        }

        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isUser = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isUser) {
            // If it's a user, exclude DRAFT events unless status is provided
            spec = spec.and((root, query, cb) -> cb.notEqual(root.get("status"), EventStatus.DRAFT));
        }

        if (isAdmin && status != null) {
            // Admin users can filter by any status, including DRAFT
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return eventRepository.findAll(spec, pageable);
    }

    public Event getEventById(Long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);

        if (eventOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }

        Event event = eventOptional.get();

        // Get the current authentication and roles
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isUser = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isUser && event.getStatus().equals(EventStatus.DRAFT)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }

        return event;
    }

    public Event createEvent(EventDto eventDto) {
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setLongDescription(eventDto.getLongDescription());
        event.setShortDescription(eventDto.getShortDescription());
        event.setDateAndTime(eventDto.getDateAndTime());
        event.setOrganizer(eventDto.getOrganizer());
        event.setLocation(eventDto.getLocation());
        event.setStatus(eventDto.getStatus() != null ? eventDto.getStatus() : EventStatus.DRAFT);
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, UpdateEventDto request) {
        return eventRepository.findById(id).map(event -> {
            if (request.getTitle() != null) event.setTitle(request.getTitle());
            if (request.getLongDescription() != null) event.setLongDescription(request.getLongDescription());
            if (request.getShortDescription() != null) event.setShortDescription(request.getShortDescription());
            if (request.getDateAndTime() != null) event.setDateAndTime(request.getDateAndTime());
            if (request.getOrganizer() != null) event.setOrganizer(request.getOrganizer());
            if (request.getLocation() != null) event.setLocation(request.getLocation());
            if (request.getStatus() != null) event.setStatus(request.getStatus());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

}
