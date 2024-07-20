package com.possible.eventbooking.controller;

import com.possible.eventbooking.dto.ResponseDto;
import com.possible.eventbooking.dto.ReservationDto;
import com.possible.eventbooking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/events")
@Tag(name = "tickets")
@RequiredArgsConstructor
public class ReservationController {


    private final ReservationService reservationService;

    @PostMapping("/{eventId}/tickets")
    public ResponseEntity<ResponseDto<Object>> reserveTickets(@PathVariable Long eventId) {
        ReservationDto reservationDto = reservationService.reserveTickets(eventId);
        ResponseDto<Object> resp = ResponseDto.builder().statusCode(200).responseMessage("Reservation created Successfully").data(reservationDto).build();
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @Operation(summary = "Get User Booked Events", description = "This endpoint returns all the User's booked event based on date span")
    @GetMapping("/users/reservations")
    public ResponseEntity<ResponseDto<Object>> viewBookedEvents() {
        List<ReservationDto> reservationDtos = reservationService.getUserBookedEvents();
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .statusCode(200)
                .responseMessage(reservationDtos.isEmpty() ? "No reservation found" : "Successfully load User booked reservation")
                .data(reservationDtos)
                .build();
        return  ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "cancel User reservation", description = "This endpoint allows User to cancel their reservation")
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseDto<Object>> cancelReservation(@PathVariable Long reservationId) {
        reservationService.deleteReservation(reservationId);
        return  ResponseEntity.ok().body(ResponseDto.builder().statusCode(200).responseMessage("Reservation deleted successfully").data(Collections.emptyList()).build());
    }
}

