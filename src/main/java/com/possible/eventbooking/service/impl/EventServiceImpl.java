package com.possible.eventbooking.service.impl;

import com.possible.eventbooking.dto.EventRequestDto;
import com.possible.eventbooking.dto.EventResponseDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.model.Category;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.repository.EventRepository;
import com.possible.eventbooking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public Event createEvent(EventRequestDto eventRequestDTO) {
        Event event = new Event();
        event.setName(eventRequestDTO.getName());
        event.setDate(eventRequestDTO.getDate());
        event.setAvailableAttendeesCount(eventRequestDTO.getAvailableAttendeesCount());
        event.setDescription(eventRequestDTO.getDescription());
        event.setVenue(eventRequestDTO.getVenue());
        event.setCategory(eventRequestDTO.getCategory());
        return eventRepository.save(event);
    }

    public List<EventResponseDto> getEvents(String name, String startDate, String endDate, String categoryValue) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.MIN;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now().plusMonths(2);
        List<Event> events;
        Category category;

        if(categoryValue != null && !categoryValue.isEmpty() ){
            category = Category.valueOf(categoryValue.toUpperCase());
            if (name != null && !name.isEmpty()){
                events = eventRepository.findByNameContainingIgnoreCaseAndDateBetweenAndCategory(name, start, end, category);
            }else {
                events = eventRepository.findByDateBetweenAndCategory(start, end, category);
            }
        }
        else if (name != null && !name.isEmpty() ){
           events = eventRepository.findByNameContainingIgnoreCaseAndDateBetween(name, start, end);
        } else {
            events = eventRepository.findAllByDate(start);
        }

        return events.stream().map(event ->  EventResponseDto.builder()
                .id(event.getId()).name(event.getName())
                .date(event.getDate())
                .availableAttendeesCount(event.getAvailableAttendeesCount())
                .description(event.getDescription())
                .category(event.getCategory())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public ResponseDto<Object> updateEvent(Long eventId, EventRequestDto eventRequestDTO) {
        Event event = eventRepository.getById(eventId);
        event.setName(eventRequestDTO.getName());
        event.setDate(eventRequestDTO.getDate());
        event.setAvailableAttendeesCount(eventRequestDTO.getAvailableAttendeesCount());
        event.setDescription(eventRequestDTO.getDescription());
        event.setCategory(eventRequestDTO.getCategory());
        event.setVenue(eventRequestDTO.getVenue());
        event = eventRepository.save(event);
        return ResponseDto.builder().statusCode(201).responseMessage("Event updated Successfully").data(event).build();
    }


}

