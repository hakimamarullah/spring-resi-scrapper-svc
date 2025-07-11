package com.starline.scrapper.model.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScrappingRequest {

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotBlank(message = "Courier code is required")
    private String courierCode;

    private String phoneLast5;
}
