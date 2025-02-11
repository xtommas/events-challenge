package com.example.Events.repositories;

import com.example.Events.models.Event;
import com.example.Events.models.UserEventId;
import com.example.Events.models.UserEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserEventRepository extends JpaRepository<UserEvent, UserEventId> {
    @Query("SELECT ue.event FROM UserEvent ue WHERE ue.user.id = :userId " +
            "AND (:pastOnly = false OR ue.event.dateAndTime < CURRENT_TIMESTAMP) " +
            "AND (:upcomingOnly = false OR ue.event.dateAndTime >= CURRENT_TIMESTAMP)")
    Page<Event> findEventsByUserId(
            @Param("userId") Long userId,
            @Param("pastOnly") boolean pastOnly,
            @Param("upcomingOnly") boolean upcomingOnly,
            Pageable pageable
    );
}
