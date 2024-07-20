package com.possible.eventbooking.model;


import com.possible.eventbooking.config.auditConfig.AuditHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Event extends AuditHistory<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Enumerated(EnumType.STRING)
    private Category category;
}

