package com.starline.scrapper.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class WebDriverPool {

    private BlockingQueue<WebDriver> pool;
    private final List<WebDriver> allDrivers = new ArrayList<>();

    @Value("${webdriver.pool.size:2}")
    private int poolSize;

    @Value("${webdriver.remote.url:http://localhost:4444/wd/hub}")
    private String seleniumRemoteUrl;

    @PostConstruct
    public void init() {
        pool = new ArrayBlockingQueue<>(poolSize);
        log.info("🚀 Creating WebDriver pool of size {} Selenium Remote URL: {}", poolSize, seleniumRemoteUrl);
        for (int i = 0; i < poolSize; i++) {
            WebDriver driver = createDriver(i);
            if (Objects.isNull(driver)) {
                continue;
            }
            allDrivers.add(driver);
            pool.add(driver);
        }
    }

    public WebDriver borrow() throws InterruptedException {
        return pool.take();
    }

    public void release(WebDriver driver) {
        if (!pool.offer(driver)) {
            log.warn("❌ Failed to release driver: {}", driver);
        }
    }

    private WebDriver createDriver(int index) {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "--headless=new",
                    "--disable-gpu",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--window-size=1280,1024",
                    "--user-agent=Mozilla/5.0 Chrome/137 Safari/537.36"
            );
            log.info("🚀 Starting WebDriver #{}", index);
            return new RemoteWebDriver(URI.create(seleniumRemoteUrl).toURL(), options);
        } catch (Exception e) {
           log.warn("Failed to create driver: {}", e.getMessage());
           return null;
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("🧹 @PreDestroy - cleaning up all WebDrivers...");
        for (WebDriver driver : allDrivers) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("❌ Failed to quit driver: {}", e.getMessage());
            }
        }
        log.info("🧹 @PreDestroy - all WebDrivers cleaned up.");
    }
}

