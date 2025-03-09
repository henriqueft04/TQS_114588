import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.platform.commons.logging.LoggerFactory.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

public class HelloWorldChromeJupiterTest {
    static final Logger log = getLogger(lookup().lookupClass());

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium-browser"); // Ensure this is the correct Chromium path

        // Add necessary arguments to avoid "unable to connect to renderer" error
        options.addArguments("--headless");
        // options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-debugging-port=9223");
        options.addArguments("--disable-gpu"); // Disable GPU acceleration
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--window-size=1920,1080"); // Ensure a virtual screen size
        options.addArguments("--disable-features=VizDisplayCompositor"); // Prevents crashes

        driver = new ChromeDriver(options);
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
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }
}
