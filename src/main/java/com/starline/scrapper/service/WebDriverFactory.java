package com.starline.scrapper.service;

import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public interface WebDriverFactory {

    WebDriver createDriver() throws MalformedURLException;

    void silentQuit(WebDriver driver);
}
