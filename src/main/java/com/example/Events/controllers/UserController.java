package com.example.Events.controllers;

import com.example.Events.dtos.UpdateEventDto;
import com.example.Events.models.Event;
import com.example.Events.models.UserEventsFilter;
import com.example.Events.services.AuthenticationService;
import com.example.Events.services.UserEventService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {
    private final UserEventService userEventService;
    private final AuthenticationService authenticationService;

    public UserController(UserEventService userEventService, AuthenticationService authenticationService) {
        this.userEventService = userEventService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/api/v1/user/events")
    public Page<Event> getAllUserEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter
            ) {
        UserEventsFilter userEventsFilter = filter != null ? UserEventsFilter.fromString(filter) : null;
        return userEventService.getAllUserEvents(page, size,filter);
    }

    @PatchMapping("/api/v1/users/{username}/promote")
    public ResponseEntity<Map<String, String>> promoteUser(@PathVariable String username) {
        try {
            authenticationService.promoteUser(username);
            return ResponseEntity.ok(Map.of("message", "user promoted successfully"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
