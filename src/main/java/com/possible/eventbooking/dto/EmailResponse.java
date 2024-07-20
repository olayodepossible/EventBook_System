package com.possible.eventbooking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailResponse {

    private Boolean success;
    private String message;
}
