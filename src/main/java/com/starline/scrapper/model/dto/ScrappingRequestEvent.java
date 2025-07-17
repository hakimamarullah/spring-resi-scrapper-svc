package com.starline.scrapper.model.dto;


import com.starline.scrapper.model.dto.event.enums.ScrappingType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScrappingRequestEvent {

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotBlank(message = "Courier code is required")
    private String courierCode;

    private String phoneLast5;

    private Long userId;

    private ScrappingType type;

    private String additionalValue1;
}
