package com.possible.eventbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private String name;
    private String email;
    private String eventName;
    private LocalDate eventDate;
    private Long reservationId;
    private int sitNumber;
}

