package com.starline.scrapper.service.impl;


import com.starline.scrapper.config.ScrapperProps;
import com.starline.scrapper.model.dto.ApiResponse;
import com.starline.scrapper.model.dto.CekResiScrapResponse;
import com.starline.scrapper.model.dto.ScrappingRequestEvent;
import com.starline.scrapper.service.ScrapperService;
import com.starline.scrapper.service.WebDriverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service("jneScrapper")
@Slf4j
@RequiredArgsConstructor
public class JNEScrapperSvc implements ScrapperService<ScrappingRequestEvent, CekResiScrapResponse> {

    private final WebDriverFactory webDriverFactory;

    private final ScrapperProps props;

    @Override
    public ApiResponse<CekResiScrapResponse> scrap(ScrappingRequestEvent payload) throws MalformedURLException {
        WebDriver driver = null;
        try {
            driver = webDriverFactory.createDriver();
            driver.get(props.getJneUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(props.getDriverWaitSeconds()));

            // ✅ Use the visible tagify input (not hidden input[name='cek-resi'])
            WebElement visibleInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".tagify__input")
            ));
            visibleInput.click();
            visibleInput.sendKeys(payload.getTrackingNumber());
            visibleInput.sendKeys(Keys.ENTER);

            // Optional: Update hidden field as fallback (no wait here!)
            try {
                WebElement hiddenInput = driver.findElement(By.name("cek-resi"));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];", hiddenInput, payload.getTrackingNumber());
            } catch (NoSuchElementException ignored) {
                log.warn("Hidden input[name='cek-resi'] not found. Continuing without JS injection.");
            }

            WebElement trackBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("lacak-pengiriman")));
            trackBtn.click();

            WebElement seeMore = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.see-more")));
            seeMore.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("num1")))
                    .sendKeys(Character.toString(payload.getPhoneLast5().charAt(0)));
            driver.findElement(By.id("num2")).sendKeys(Character.toString(payload.getPhoneLast5().charAt(1)));
            driver.findElement(By.id("num3")).sendKeys(Character.toString(payload.getPhoneLast5().charAt(2)));
            driver.findElement(By.id("num4")).sendKeys(Character.toString(payload.getPhoneLast5().charAt(3)));
            driver.findElement(By.id("num5")).sendKeys(Character.toString(payload.getPhoneLast5().charAt(4)));

            driver.findElement(By.id("send-question")).click();

            // 🔄 Wait for tab switch
            WebDriverWait tabWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            tabWait.until(d -> d.getWindowHandles().size() > 1);
            List<String> tabs = driver.getWindowHandles().stream().toList();
            if (tabs.size() > 1) {
                driver.switchTo().window(tabs.get(1));
            } else {
                log.warn("No new tab opened.");
                return ApiResponse.setResponse(CekResiScrapResponse.builder().build(), "Verification tab not opened.", 500);
            }

            // ✅ Extract last timeline entry
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.timeline.widget li")));

            Document doc = Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
            Elements events = doc.select("ul.timeline.widget li");


            if (!events.isEmpty()) {
                Element last = events.last();
                String timestamp = getText(last, ".byline span");
                String checkpoint = getText(last, "h2.title a");
                return ApiResponse.setResponse(CekResiScrapResponse.builder()
                        .timestamp(timestamp)
                        .checkpoint(checkpoint)
                        .build(), 200);
            } else {
                return ApiResponse.setResponse(CekResiScrapResponse.builder().build(), "No timeline entry found.", 200);
            }

        } finally {
            webDriverFactory.silentQuit(driver);
        }

    }

    private String getText(Element element, String cssQuery) {
        return Optional.ofNullable(element)
                .map(it -> it.select(cssQuery))
                .map(Elements::text)
                .orElse(null);
    }
}
