package com.possible.eventbooking.service.impl;

import com.possible.eventbooking.config.auditConfig.AuditorAwareImpl;
import com.possible.eventbooking.dto.EventResponseDto;
import com.possible.eventbooking.dto.ReservationDto;
import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.exception.ResourceNotFoundException;
import com.possible.eventbooking.model.Event;
import com.possible.eventbooking.model.Reservation;
import com.possible.eventbooking.model.User;
import com.possible.eventbooking.repository.EventRepository;
import com.possible.eventbooking.repository.ReservationRepository;
import com.possible.eventbooking.repository.UserRepository;
import com.possible.eventbooking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final EventRepository eventRepository;


    private final UserRepository userRepository;
    private final AuditorAwareImpl auditorAware;

    public ReservationDto reserveTickets(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        int attendeeCount = event.getAvailableAttendeesCount();
        if ( attendeeCount < 0) {
            throw new ResourceNotFoundException("Not enough tickets available");
        }

        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        int remainingAttendee = attendeeCount - 1;
        Reservation reservation = new Reservation();
        reservation.setEvent(event);
        reservation.setUser(user);
        reservation.setAttendeesCount(attendeeCount - remainingAttendee);
        reservation = reservationRepository.save(reservation);

        event.setAvailableAttendeesCount(remainingAttendee);
        eventRepository.save(event);
        return ReservationDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .eventName(event.getName())
                .eventDate(event.getDate())
                .reservationId(reservation.getId())
                .sitNumber(reservation.getAttendeesCount())
                .build();
    }

    @Override
    public List<ReservationDto>  getUserBookedEvents() {
        Long userId = getCurrentUserId();
        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        return  reservations.stream().map(reservation -> ReservationDto.builder()
                .name(reservation.getUser().getName())
                .email(reservation.getUser().getEmail())
                .eventName(reservation.getEvent().getName())
                .eventDate(reservation.getEvent().getDate())
                .reservationId(reservation.getId())
                .sitNumber(reservation.getAttendeesCount())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        Event event = reservation.getEvent();
        event.setAvailableAttendeesCount(event.getAvailableAttendeesCount() + 1);
        eventRepository.save(event);
        reservationRepository.delete(reservation);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        return 0L;
    }
}
