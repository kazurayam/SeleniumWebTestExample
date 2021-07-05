package com.kazurayam.seleniumdemo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

//@Disabled
public class DemoSeleniumTest {

    private static Logger logger = LoggerFactory.getLogger(DemoSeleniumTest.class);

    private WebDriver driver;

    @BeforeAll
    public static void beforeAll() {
        logger.info("@BeforeAll was called");
    }

    @BeforeEach
    public void setUp()
    {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.navigate().to("https://the-internet.herokuapp.com/login");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.MILLISECONDS);
    }

    @Test
    public void userLogin()
    {
        assert driver != null;
        WebElement usernameTxt = driver.findElement(By.id("username"));
        usernameTxt.sendKeys("tomsmith");
        WebElement passwordTxt = driver.findElement(By.id("password"));
        passwordTxt.sendKeys("SuperSecretPassword!");
        WebElement submitBtn = driver.findElement(By.className("radius"));
        submitBtn.click();
        logger.info("Current URL is:" + driver.getCurrentUrl());
        Assertions.assertTrue(driver.getCurrentUrl().contains("secure"));
    }

    @AfterEach
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void afterAll() {
        logger.info("@AfterAll was called");
    }

}
