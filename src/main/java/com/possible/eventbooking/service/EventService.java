package com.possible.eventbooking.service;

import com.possible.eventbooking.dto.EventRequestDto;
import com.possible.eventbooking.dto.EventResponseDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.model.Event;

import java.util.List;


public interface EventService {
    Event createEvent(EventRequestDto eventRequestDTO);

    List<EventResponseDto> getEvents(String name, String startDate, String endDate, String category);

    ResponseDto<Object> updateEvent(Long eventId, EventRequestDto dto);
}
