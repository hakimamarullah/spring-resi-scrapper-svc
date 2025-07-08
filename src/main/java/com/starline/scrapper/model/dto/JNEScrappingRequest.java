package com.starline.scrapper.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JNEScrappingRequest extends ScrappingRequest {

    private String phoneLast5;
}
