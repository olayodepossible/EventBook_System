package com.possible.eventbooking.service;

import com.possible.eventbooking.dto.ReservationDto;

import java.util.List;

public interface ReservationService {
    ReservationDto reserveTickets(Long eventId);

    List<ReservationDto> getUserBookedEvents();
    void deleteReservation(Long reservationId);
}
