package com.example.Events.models;

import java.io.Serializable;

public class UserEventId implements Serializable {
    private Long user;
    private Long event;

    public UserEventId() {}

    public UserEventId(Long user, Long event) {
        this.user = user;
        this.event = event;
    }

    public Long getUserId() {
        return user;
    }

    public void setUserId(Long userId) {
        this.user = userId;
    }

    public Long getEvent() {
        return event;
    }

    public void setEvent(Long event) {
        this.event = event;
    }
}
