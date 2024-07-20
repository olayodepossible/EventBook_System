package com.possible.eventbooking.dto;

import com.possible.eventbooking.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDto {

    private Long id;
    private String name;
    private LocalDate date;
    private int availableAttendeesCount;
    private String description;
    private String venue;
    private Category category;
}

