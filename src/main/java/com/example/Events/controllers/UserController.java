package com.example.Events.controllers;

import com.example.Events.models.Event;
import com.example.Events.models.UserEventsFilter;
import com.example.Events.services.UserEventService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserEventService userEventService;

    public UserController(UserEventService userEventService) {
        this.userEventService = userEventService;
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
}
