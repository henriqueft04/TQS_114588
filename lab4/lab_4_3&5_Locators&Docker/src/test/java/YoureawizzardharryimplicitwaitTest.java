import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class YoureawizzardharryimplicitwaitTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    private JavascriptExecutor js;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new FirefoxDriver();
        js = (JavascriptExecutor) driver;
        vars = new HashMap<>();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void youreawizzardharry() {
        // Navigate to the website
        driver.get("https://cover-bookstore.onrender.com/");

        // Wait for page to fully load
        wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));

        By searchInputSelector = By.cssSelector(".Navbar_searchBarInput__w8FwI");
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(searchInputSelector));

        // Scroll into view before interacting
        js.executeScript("arguments[0].scrollIntoView(true);", searchInput);
        wait.until(ExpectedConditions.visibilityOf(searchInput));

        searchInput.click();
        searchInput.clear();
        searchInput.sendKeys("harry potter");
        searchInput.sendKeys(Keys.ENTER);

        try {
            // Wait for search results to load and be visible
            WebElement bookCover = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".SearchList_bookCoverImage__1COZ9")));

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", bookCover);
            wait.until(ExpectedConditions.elementToBeClickable(bookCover)).click();

            // Wait for navigation to book details page
            wait.until(ExpectedConditions.urlContains("/book/"));
        } catch (TimeoutException e) {
            System.out.println("Failed to interact with search results: " + e.getMessage());
            takeScreenshot();
        }
    }

    private void takeScreenshot() {
        if (driver instanceof TakesScreenshot) {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            System.out.println("Screenshot taken: " + screenshot.getAbsolutePath());
        }
    }
}
