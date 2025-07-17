package com.starline.scrapper.service.impl;

import com.starline.scrapper.service.WebDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class WebDriverFactoryImpl implements WebDriverFactory {

    @Value("${webdriver.remote.url:http://localhost:4444/wd/hub}")
    private String seleniumRemoteUrl;

    public WebDriver createDriver() throws MalformedURLException {
        ChromeOptions options = getChromeOptions();

        // Remove automation flags
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new RemoteWebDriver(URI.create(seleniumRemoteUrl).toURL(), options);

        // Inject JS to spoof navigator.webdriver and others
        spoofNavigatorFlags((JavascriptExecutor) driver);

        return driver;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Anti-bot detection tweaks
        options.addArguments(
                "--headless=new",
                "--disable-blink-features=AutomationControlled",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--disable-infobars",
                "--window-size=1280,1024",
                "--start-maximized",
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/137 Safari/537.36"
        );
        return options;
    }

    private void spoofNavigatorFlags(JavascriptExecutor js) {
        js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        js.executeScript("navigator.languages = ['en-US', 'en']");
        js.executeScript("navigator.plugins = [1, 2, 3, 4, 5]");
    }


    @Override
    public void silentQuit(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Failed to quit driver {}", e.getMessage());
            }
        }
    }

}

