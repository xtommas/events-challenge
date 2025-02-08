package com.example.Events.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_events")
@IdClass(UserEventId.class)
public class UserEvent {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public UserEvent() {
    }

    public UserEvent(User user, Event event) {
        this.user = user;
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
