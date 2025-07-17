package com.starline.scrapper.service.impl;

import com.starline.scrapper.config.ScrapperProps;
import com.starline.scrapper.exceptions.DataNotFoundException;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequestEvent;
import com.starline.scrapper.service.ScrapperService;
import com.starline.scrapper.service.WebDriverFactory;
import com.starline.scrapper.utils.SeleniumUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Objects;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class CekResiScrapperSvc implements ScrapperService<ScrappingRequestEvent, CekResiScrapResponse> {

    private final WebDriverFactory webDriverFactory;

    private final ScrapperProps props;

    @Override
    public ApiResponse<CekResiScrapResponse> scrap(ScrappingRequestEvent payload) throws InterruptedException, MalformedURLException {
        WebDriver driver = null;
        try {
            driver = webDriverFactory.createDriver();
            String trackingUrl = props.getCekResiUrl() + payload.getTrackingNumber();
            driver.get(trackingUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(props.getDriverWaitSeconds()));

            // Click CEKRESI button
            WebElement cekresiBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'CEKRESI')]")));
            cekresiBtn.click();

            // Select courier
            WebElement courierBtn = SeleniumUtils.waitUntilOrThrow(wait,
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(@onclick, \"setExp('" + payload.getCourierCode() + "')\")]")),
                    () -> new DataNotFoundException("Please Check Your Tracking Number or Courier Code"));
            courierBtn.click();

            // Expand journey section
            WebElement accordion = SeleniumUtils.waitUntilOrThrow(wait,
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(),'Lihat perjalanan paket')]")),
                    () -> new DataNotFoundException("Tracking Data Not Found"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", accordion);
            Thread.sleep(1000);
            accordion.click();
            Thread.sleep(2000);

            // Parse HTML
            Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
            Elements rows = doc.select(".panel-group .panel:last-of-type tr");

            if (rows.size() >= 2) {
                Element first = rows.get(1);
                Element last = rows.getLast();

                String firstTimestamp = Objects.requireNonNull(first.select("td").first()).text();
                String firstCheckpoint = first.select("td").get(1).text();

                String lastTimestamp = Objects.requireNonNull(last.select("td").first()).text();
                String lastCheckpoint = last.select("td").get(1).text();

                boolean lastIsLatest = lastTimestamp.compareTo(firstTimestamp) >= 0;

                return ApiResponse.setSuccess(CekResiScrapResponse.builder()
                        .timestamp(lastIsLatest ? lastTimestamp : firstTimestamp)
                        .checkpoint(lastIsLatest ? lastCheckpoint : firstCheckpoint)
                        .build());
            }

            return ApiResponse.setResponse(CekResiScrapResponse.builder().build(), "No data found", 200);

        } catch (Exception e) {
            log.warn("Failed to scrap for: {} Reason: {}", payload.getTrackingNumber(), e.getMessage());
            Thread.currentThread().interrupt();
            throw e;
        } finally {
            webDriverFactory.silentQuit(driver);
        }

    }


}
