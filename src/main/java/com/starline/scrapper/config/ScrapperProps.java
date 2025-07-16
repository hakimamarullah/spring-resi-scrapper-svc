package com.starline.scrapper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scrapper")
@Setter
@Getter
public class ScrapperProps {

    private long driverWaitSeconds = 60;
    private String cekResiUrl = "https://cekresi.com/?v=wi1&noresi=";
    private String jneUrl = "https://www.jne.co.id/en/tracking-package";
}
