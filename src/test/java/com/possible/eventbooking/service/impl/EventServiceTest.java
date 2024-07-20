package com.possible.eventbooking.service.impl;

import com.possible.eventbooking.dto.EventRequestDto;
import com.possible.eventbooking.dto.EventResponseDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.model.Category;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private EventRequestDto eventRequestDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventRequestDto = new EventRequestDto();
        eventRequestDto.setName("Test Event");
        eventRequestDto.setDate(LocalDate.of(2024, 8, 1));
        eventRequestDto.setAvailableAttendeesCount(100);
        eventRequestDto.setDescription("Test Description");
        eventRequestDto.setVenue("City");
        eventRequestDto.setCategory(Category.GAME);

        event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setDate(LocalDate.of(2024, 8, 1));
        event.setAvailableAttendeesCount(100);
        event.setDescription("Test Description");
        event.setVenue("city");
        event.setCategory(Category.GAME);
    }

    @Test
    void testCreateEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(eventRequestDto);

        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getName());
        assertEquals(LocalDate.of(2024, 8, 1), createdEvent.getDate());
        assertEquals(100, createdEvent.getAvailableAttendeesCount());
        assertEquals("Test Description", createdEvent.getDescription());
        assertEquals(Category.GAME, createdEvent.getCategory());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testGetEventsByNameAndCategory() {
        when(eventRepository.findByNameContainingIgnoreCaseAndDateBetweenAndCategory(eq("Test"), any(LocalDate.class), any(LocalDate.class), eq(Category.GAME)))
                .thenReturn(Collections.singletonList(event));

        List<EventResponseDto> events = eventService.getEvents("Test", null, null, "Game");

        assertNotNull(events);
        assertEquals(1, events.size());
        EventResponseDto eventResponse = events.get(0);
        assertEquals("Test Event", eventResponse.getName());
        assertEquals(LocalDate.of(2024, 8, 1), eventResponse.getDate());
        assertEquals(100, eventResponse.getAvailableAttendeesCount());
        assertEquals("Test Description", eventResponse.getDescription());
        assertEquals(Category.GAME, eventResponse.getCategory());

        verify(eventRepository, times(1)).findByNameContainingIgnoreCaseAndDateBetweenAndCategory(eq("Test"), any(LocalDate.class), any(LocalDate.class), eq(Category.GAME));
    }

    @Test
    void testGetEventsByName() {
        when(eventRepository.findByNameContainingIgnoreCaseAndDateBetween(eq("Test"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(event));

        List<EventResponseDto> events = eventService.getEvents("Test", null, null, "");

        assertNotNull(events);
        assertEquals(1, events.size());
        EventResponseDto eventResponse = events.get(0);
        assertEquals("Test Event", eventResponse.getName());
        assertEquals(LocalDate.of(2024, 8, 1), eventResponse.getDate());
        assertEquals(100, eventResponse.getAvailableAttendeesCount());
        assertEquals("Test Description", eventResponse.getDescription());
        assertEquals(Category.GAME, eventResponse.getCategory());

        verify(eventRepository, times(1)).findByNameContainingIgnoreCaseAndDateBetween(eq("Test"), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetEventsByCategory() {
        when(eventRepository.findByDateBetweenAndCategory(any(LocalDate.class), any(LocalDate.class), eq(Category.GAME)))
                .thenReturn(Collections.singletonList(event));

        List<EventResponseDto> events = eventService.getEvents("", null, null, "Game");

        assertNotNull(events);
        assertEquals(1, events.size());
        EventResponseDto eventResponse = events.get(0);
        assertEquals("Test Event", eventResponse.getName());
        assertEquals(LocalDate.of(2024, 8, 1), eventResponse.getDate());
        assertEquals(100, eventResponse.getAvailableAttendeesCount());
        assertEquals("Test Description", eventResponse.getDescription());
        assertEquals(Category.GAME, eventResponse.getCategory());

        verify(eventRepository, times(1)).findByDateBetweenAndCategory(any(LocalDate.class), any(LocalDate.class), eq(Category.GAME));
    }

    @Test
    void testGetAllEvents() {
        when(eventRepository.findAllByDate(any(LocalDate.class)))
                .thenReturn(Collections.singletonList(event));

        List<EventResponseDto> events = eventService.getEvents("", null, null, "");

        assertNotNull(events);
        assertEquals(1, events.size());
        EventResponseDto eventResponse = events.get(0);
        assertEquals("Test Event", eventResponse.getName());
        assertEquals(LocalDate.of(2024, 8, 1), eventResponse.getDate());
        assertEquals(100, eventResponse.getAvailableAttendeesCount());
        assertEquals("Test Description", eventResponse.getDescription());
        assertEquals(Category.GAME, eventResponse.getCategory());

        verify(eventRepository, times(1)).findAllByDate(any(LocalDate.class));
    }

    @Test
    void testUpdateEvent() {
        eventRequestDto.setName("Updated Event");
        eventRequestDto.setDate(LocalDate.of(2024, 8, 2));
        eventRequestDto.setAvailableAttendeesCount(200);
        eventRequestDto.setDescription("Updated Description");
        eventRequestDto.setVenue("Lagoon");
        when(eventRepository.getById(1L)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        ResponseDto<Object> response = eventService.updateEvent(1L, eventRequestDto);

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getResponseMessage()).isEqualTo("Event updated Successfully");

        Event updatedEvent = (Event) response.getData();
        assertThat(updatedEvent.getName()).isEqualTo("Updated Event");
        assertThat(updatedEvent.getDate()).isEqualTo(LocalDate.of(2024, 8, 2));
        assertThat(updatedEvent.getAvailableAttendeesCount()).isEqualTo(200);
        assertThat(updatedEvent.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedEvent.getCategory()).isEqualTo(Category.GAME);
        assertThat(updatedEvent.getVenue()).isEqualTo("Lagoon");

        verify(eventRepository, times(1)).getById(1L);
        verify(eventRepository, times(1)).save(event);
    }
}
