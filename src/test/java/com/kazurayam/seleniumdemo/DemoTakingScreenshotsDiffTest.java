package com.kazurayam.seleniumdemo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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

    private WebDriver driver;

    // Device Pixel Ratio. 2.0f on my Mac Book Air
    private float dpr = 2.0f;

    private int timeout = 150; // milli-seconds

    private Path reportDir;

    @BeforeEach
    public void setUp() throws IOException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.navigate().to("http://demoaut.katalon.com");
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.MILLISECONDS);

        // resolve Device Pixel Ratio by making query to Chrome browser

        //
        reportDir = Paths.get(".").resolve("build").resolve("tmp").resolve("DemoTakingScreenshotsDiffTest");
        if (Files.exists(reportDir)) {
            // delete the directory to clear out
            Files.walk(reportDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(reportDir);
    }

    @Test
    public void takeScreenshotsAndDiff()
    {
        assert driver != null;
        WebElement aMenu = driver.findElement(By.id("menu-toggle"));
        // take the 1st screenshot
        driver.manage().window().maximize();
        Screenshot expected = new AShot()
                .coordsProvider(new WebDriverCoordsProvider()) //find coordinates with WebDriver API
                .shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(dpr), timeout))
                .takeScreenshot(driver, aMenu);
        // take the 2nd screenshot
        driver.manage().window().setSize(new Dimension(800, 800));
        Screenshot actual = new AShot()
                .coordsProvider(new WebDriverCoordsProvider()) //find coordinates with WebDriver API
                .shootingStrategy(ShootingStrategies.viewportPasting(ShootingStrategies.scaling(dpr), timeout))
                .takeScreenshot(driver, aMenu);// make diff
        // find a difference between the 2 images
        // using com.kazurayam.seleniumdemo.ImageDifference class which wraps AShot
        ImageDifference imageDifference = new ImageDifference(expected.getImage(), actual.getImage());
        // report it into a HTML file
        Reporter reporter = new Reporter(reportDir);
        reporter.add(imageDifference);
        reporter.report();
        // verify the magnitude of image difference
        imageDifference.setCriteria(10.0d);
        Assertions.assertTrue(imageDifference.imagesAreDifferent());
    }

    @AfterEach
    public void tearDown(){
        if (driver != null) {
            driver.quit();
        }
    }

}
