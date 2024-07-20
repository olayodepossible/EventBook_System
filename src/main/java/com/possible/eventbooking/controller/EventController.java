package com.possible.eventbooking.controller;

import com.possible.eventbooking.dto.EventRequestDto;
import com.possible.eventbooking.dto.EventResponseDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.*;

@RestController
@RequestMapping("/events")
@Tag(name = "events")
@RequiredArgsConstructor
public class EventController {


    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ResponseDto<Object>> createEvent(@Valid @RequestBody EventRequestDto eventRequestDTO) {
       Event event = eventService.createEvent(eventRequestDTO);
        ResponseDto<Object> resp = ResponseDto.builder().statusCode(201).responseMessage("Event created Successfully").data(event).build();
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<Object>> getEvents(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String startDate,
                                                          @RequestParam(required = false) String endDate,
                                                          @RequestParam(required = false) String category) {
       List<EventResponseDto> eventResponseDtos = eventService.getEvents(name, startDate, endDate, category);

       ResponseDto<Object> resp =  ResponseDto.builder()
                .statusCode(200)
                .responseMessage(eventResponseDtos.isEmpty() ?"No data found" : "Successfully load Events")
                .data(eventResponseDtos)
                .build();
        return ResponseEntity.ok(resp);
    }

}

