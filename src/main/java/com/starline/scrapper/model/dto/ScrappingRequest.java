package com.starline.scrapper.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScrappingRequest {

    private String trackingNumber;

    @NotNull(message = "Courier code is required")
    private String courierCode;

    private String phoneLast5;
}
