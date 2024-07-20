package com.possible.eventbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.possible.eventbooking.dto.EventRequestDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.model.Category;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    private EventRequestDto eventRequestDto;
    private Event event;

    @BeforeEach
    void setUp() {
        eventRequestDto = new EventRequestDto();
        eventRequestDto.setName("Test Event");
        eventRequestDto.setDate(LocalDate.of(2024, 2, 1));
        eventRequestDto.setAvailableAttendeesCount(100);
        eventRequestDto.setDescription("Test Description");
        eventRequestDto.setVenue("City");
        eventRequestDto.setCategory(Category.CONCERT);

        event = new Event();
        event.setName("Test Event");
        event.setDate(LocalDate.of(2023, 8, 1));
        event.setAvailableAttendeesCount(100);
        event.setDescription("Test Description");
        event.setVenue("City");
        event.setCategory(Category.CONCERT);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCreateEvent() throws Exception {
        MvcResult result = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ResponseDto<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getResponseMessage()).isEqualTo("Event created Successfully");

        LinkedHashMap<String, Object> eventMap = (LinkedHashMap<String, Object>) response.getData();
        System.out.println(eventMap);
        Event savedEvent = objectMapper.convertValue(eventMap, Event.class);
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getName()).isEqualTo(eventRequestDto.getName());
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testGetEvents_Empty_Param() throws Exception {
        eventRepository.save(event);

        MvcResult result = mockMvc.perform(get("/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDto<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getResponseMessage()).isEqualTo("No data found");
        assertThat(response.getData()).isNotNull();
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testGetEvents() throws Exception {
        eventRepository.save(event);

        MvcResult result = mockMvc.perform(get("/events?name=&startDate=2023-04-24&category=Concert&endDate=2024-08-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDto<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getResponseMessage()).isEqualTo("Successfully load Events");
        assertThat(response.getData()).isNotNull();
    }
}
