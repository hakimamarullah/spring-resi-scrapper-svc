package com.starline.scrapper.model.dto.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScrapingResponse {

    private String courier;
    private String resi;

    @JsonProperty("result")
    private List<AWBInfo> awbInfos;
}
