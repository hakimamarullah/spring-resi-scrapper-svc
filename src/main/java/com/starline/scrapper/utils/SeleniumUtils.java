package com.starline.scrapper.utils;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.function.Function;
import java.util.function.Supplier;

public class SeleniumUtils {

    private SeleniumUtils() {

    }

    public static <T> T waitUntilOrThrow(
            WebDriverWait wait,
            Function<WebDriver, T> condition,
            Supplier<? extends RuntimeException> toThrow) {

        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            throw toThrow.get();
        }
    }
}

