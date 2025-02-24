package com.example.Events.services;

import com.example.Events.models.*;
import com.example.Events.repositories.EventRepository;
import com.example.Events.repositories.UserEventRepository;
import com.example.Events.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class UserEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserEventRepository userEventRepository;

    public UserEventService(EventRepository eventRepository, UserRepository userRepository, UserEventRepository userEventRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userEventRepository = userEventRepository;
    }

    public Page<Event> getAllUserEvents(int page, int size, String filter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime now = LocalDateTime.now();

        boolean pastOnly = false;
        boolean upcomingOnly = false;

        if (filter != null) {
            if ("past".equalsIgnoreCase(filter)) {
                pastOnly = true;
            } else if ("upcoming".equalsIgnoreCase(filter)) {
                upcomingOnly = true;
            } else {
                throw new IllegalArgumentException("Invalid filter value: " + filter);
            }
        }

        return userEventRepository.findEventsByUserId(user.getId(), pastOnly, upcomingOnly, pageable);
    }

    public UserEvent signUpForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getStatus().equals(EventStatus.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }

        if (event.getDateAndTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot sign up for past events");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userEventRepository.existsById(new UserEventId(user.getId(), event.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already signed up for event");
        }

        UserEvent userEvent = new UserEvent(user, event);
        return userEventRepository.save(userEvent);
    }
}
