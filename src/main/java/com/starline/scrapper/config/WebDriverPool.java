package com.starline.scrapper.config;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;

@Component
@Slf4j
public class WebDriverPool {

    @Value("${webdriver.remote.url:http://localhost:4444/wd/hub}")
    private String seleniumRemoteUrl;

    @Bean
    public WebDriver mainDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1280,1024",
                "--user-agent=Mozilla/5.0 Chrome/137 Safari/537.36"
        );

        return new RemoteWebDriver(URI.create(seleniumRemoteUrl).toURL(), options);
    }

}

