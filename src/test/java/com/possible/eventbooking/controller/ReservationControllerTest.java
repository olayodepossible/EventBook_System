package com.possible.eventbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.possible.eventbooking.dto.ReservationDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.model.Category;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.model.Reservation;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.repository.EventRepository;
import com.possible.eventbooking.repository.ReservationRepository;
import com.possible.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Event event;
    private User user;
    private Reservation reservation;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user = userRepository.save(user);

        event = new Event();
        event.setName("Test Event");
        event.setDate(LocalDate.of(2024, 2, 1));
        event.setAvailableAttendeesCount(100);
        event.setDescription("Test Description");
        event.setCategory(Category.GAME);
        event.setVenue("City");
        event = eventRepository.save(event);

        reservation = new Reservation();
        reservation.setEvent(event);
        reservation.setUser(user);
        reservation.setAttendeesCount(1);
        reservation = reservationRepository.save(reservation);
    }


    @Test
    @WithMockUser(username = "testuser", authorities = "USER")
    void testReserveTickets() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("USER"))
        );

        MvcResult result = mockMvc.perform(post("/events/{eventId}/tickets", event.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ResponseDto<?> response= objectMapper.readValue(result.getResponse().getContentAsString(), ResponseDto.class);

        LinkedHashMap<String, Object> reservationMap = (LinkedHashMap<String, Object>) response.getData();

        ReservationDto reservationDto = objectMapper.convertValue(reservationMap, ReservationDto.class);
        // Validate the response
        assertThat(reservationDto.getName()).isEqualTo(user.getName());
        assertThat(reservationDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(reservationDto.getEventName()).isEqualTo(event.getName());


        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        assertThat(reservations).isNotEmpty();
        assertThat(reservations.size()).isEqualTo(2);
        assertThat(reservations.get(0).getEvent().getId()).isEqualTo(event.getId());
        assertThat(eventRepository.findById(event.getId()).get().getAvailableAttendeesCount()).isEqualTo(99);
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testViewBookedEvents() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("USER"))
        );
        mockMvc.perform(get("/events/users/reservations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.responseMessage").value("Successfully load User booked reservation"))
                .andExpect(jsonPath("$.data[0].eventName").value(event.getName()));
    }

    @Test
    @WithMockUser(authorities = "USER")
    void testCancelReservation() throws Exception {
        event.setAvailableAttendeesCount(99);
        event = eventRepository.save(event);
        mockMvc.perform(delete("/events/reservations/{reservationId}", reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.responseMessage").value("Reservation deleted successfully"));

        Optional<Reservation> deletedReservation = reservationRepository.findById(reservation.getId());
        assertThat(deletedReservation).isEmpty();

        Event updatedEvent = eventRepository.findById(event.getId()).orElse(null);
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getAvailableAttendeesCount()).isEqualTo(100);
    }
}
