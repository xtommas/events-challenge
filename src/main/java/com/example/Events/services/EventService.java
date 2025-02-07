package com.example.Events.services;

import com.example.Events.dtos.UpdateEventDto;
import com.example.Events.models.Event;
import com.example.Events.repositories.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
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
