package com.example.Events.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserEventsFilter {

    PAST("past"), UPCOMING("upcoming");

    private final String value;

    UserEventsFilter(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserEventsFilter fromString(String value) {
        for (UserEventsFilter filter : UserEventsFilter.values()) {
            if (filter.value.equalsIgnoreCase(value)) {
                return filter;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
}
