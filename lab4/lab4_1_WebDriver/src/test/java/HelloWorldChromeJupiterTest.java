import io.github.bonigarcia.seljup.SeleniumJupiter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.platform.commons.logging.LoggerFactory.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SeleniumJupiter.class)
public class HelloWorldChromeJupiterTest {
    static final Logger log = getLogger(lookup().lookupClass());

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setup() {
        driver = new FirefoxDriver();
    }

    @Test
    void test() {
        // Exercise
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        driver.get(sutUrl);
        String title = driver.getTitle();
        log.debug( () -> (String.format("The title of %s is %s", sutUrl, title)));

        // Verify
        assertThat(title).isEqualTo("Hands-On Selenium WebDriver with Java");

        WebElement slowCalculator = driver.findElement(By.xpath("//a[contains(text(),'Slow calculator')]"));
        slowCalculator.click();


        String expectedUrl = "https://bonigarcia.dev/selenium-webdriver-java/slow-calculator.html";
        String actualUrl = driver.getCurrentUrl();

        log.debug(() -> String.format("Navigated to: %s", actualUrl));
        assertThat(actualUrl).isEqualTo(expectedUrl);
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }
}
