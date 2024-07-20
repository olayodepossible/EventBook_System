package com.possible.eventbooking.dto;

import com.possible.eventbooking.model.Category;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class EventRequestDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private LocalDate date;

    @Min(1)
    @Max(1000)
    private int availableAttendeesCount;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 100)
    private String venue;

    @NotNull
    private Category category;
}

