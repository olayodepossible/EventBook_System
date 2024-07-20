package com.possible.eventbooking.repository;

import com.possible.eventbooking.model.Category;
import com.possible.eventbooking.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByNameContainingIgnoreCaseAndDateBetweenAndCategory(String name, LocalDate startDate, LocalDate endDate, Category category);
    List<Event> findByNameContainingIgnoreCaseAndDateBetween(String name, LocalDate startDate, LocalDate endDate);

    List<Event> findByDateBetweenAndCategory(LocalDate startDate, LocalDate endDate, Category category);

    List<Event> findAllByDate(LocalDate localDate);
}

