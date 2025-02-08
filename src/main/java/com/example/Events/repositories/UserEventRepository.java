package com.example.Events.repositories;

import com.example.Events.models.UserEventId;
import com.example.Events.models.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventRepository extends JpaRepository<UserEvent, UserEventId> {
}
