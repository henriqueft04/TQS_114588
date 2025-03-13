import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;
import java.time.Duration;

public class YoureawizzardharryTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;

  @BeforeEach
  public void setUp() {
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();

    driver.manage().window().maximize();
  }

  @AfterEach
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void youreawizzardharry() throws InterruptedException {
    // Navigate to the website
    driver.get("https://cover-bookstore.onrender.com/");

    // Create wait object for explicit waits
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    // Wait for page to load completely
    wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

    By searchInputSelector = By.cssSelector(".Navbar_searchBarInput__w8FwI");
    WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(searchInputSelector));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchInput);
    Thread.sleep(1000); // Small pause to let page settle

    searchInput.click();
    searchInput.clear();
    searchInput.sendKeys("harry potter");
    searchInput.sendKeys(Keys.ENTER);

    try {
      WebElement bookCover = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".SearchList_bookCoverImage__1COZ9")));

      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", bookCover);
      Thread.sleep(1500); // Give more time for the page to settle

      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookCover);

      wait.until(ExpectedConditions.urlContains("/book/"));
    } catch (Exception e) {
      System.out.println("Failed to interact with search results: " + e.getMessage());
      if (driver instanceof TakesScreenshot) {
        ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        System.out.println("Screenshot taken");
      }
    }
  }
}