package com.possible.eventbooking.service.impl;


import com.possible.eventbooking.config.auditConfig.AuditorAwareImpl;
import com.possible.eventbooking.dto.ReservationDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.exception.ResourceNotFoundException;
import com.possible.eventbooking.exception.UserNotFoundException;
import com.possible.eventbooking.model.Category;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.model.Reservation;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.repository.EventRepository;
import com.possible.eventbooking.repository.ReservationRepository;
import com.possible.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditorAwareImpl auditorAware;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User user;
    private Event event;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setDate(LocalDate.of(2024, 8, 1));
        event.setAvailableAttendeesCount(100);
        event.setDescription("Test Description");
        event.setCategory(Category.GAME);
        event.setVenue("Test Venue");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setEvent(event);
        reservation.setUser(user);
        reservation.setAttendeesCount(1);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "USER")
    void testReserveTickets() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("USER"))
        );
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationDto reservationDto = reservationService.reserveTickets(1L);

        assertThat(reservationDto.getName()).isEqualTo(user.getName());
        assertThat(reservationDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(reservationDto.getEventName()).isEqualTo(event.getName());

        verify(eventRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testReserveTicketsEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.reserveTickets(1L));

        verify(eventRepository, times(1)).findById(1L);
        verify(userRepository, times(0)).findById(anyLong());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }


    @Test
    @WithMockUser(username = "testuser", authorities = "USER")
    void testGetUserBookedEvents() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("USER"))
        );
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(reservationRepository.findByUserId(1L)).thenReturn(List.of(reservation));

        List<ReservationDto> reservationDtos= reservationService.getUserBookedEvents();
        assertThat(reservationDtos).hasSize(1);

        verify(reservationRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testDeleteReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.deleteReservation(1L);

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).delete(reservation);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testDeleteReservationNotFound() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.deleteReservation(1L));

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(0)).delete(any(Reservation.class));
        verify(eventRepository, times(0)).save(any(Event.class));
    }
}
