package com.kazurayam.seleniumdemo;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.concurrent.TimeUnit;

/**
 * - visit a web site "http://demoaut.katalon.core"
 * - take 2 screenshots of a web element '<a id="menu-toggle"></a>'
 * - once with the view port size width = 400
 * - another with the view port size width maximum
 * - 2 images will be similar but actually different for 40% pixel-size
 * - compare 2 images to generate a diff image
 * - save 3 PNG files
 * - compile a HTML view of the 3 images
 * - throw failure when the diff ratio > 10%, otherwise pass the test.
 * - using Selenium WebDriver with Chrome Browser
 * - using AShot library
 */
public class DemoTakingScreenshotsDiffTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setUp()
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
        System.out.println("Current URL is:" + driver.getCurrentUrl());
        Assertions.assertTrue(driver.getCurrentUrl().contains("secure"));
    }

    @AfterAll
    public static void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

}
