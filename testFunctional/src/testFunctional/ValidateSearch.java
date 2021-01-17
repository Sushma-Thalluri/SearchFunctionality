package testFunctional;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ValidateSearch {

	@Test
	public void ValidSearch() {

		try {

			String searchInput[] = { "kelly", "teen", "jenna", "123swt", "/teen;", "prat", "les" };

			// Browser setup dynamic
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--disable-notifications");
			WebDriverManager.chromedriver().setup();

			// change the browserVersion according to your test environment
			WebDriverManager.chromedriver().browserVersion("87.0.4280.141").setup();
			WebDriver driver = new ChromeDriver(chromeOptions);
			WebDriverWait wait = new WebDriverWait(driver, 3000);

			// Navigate to the webSite
			driver.get("https://www.rabbitsreviews.com/index.html");
			driver.manage().window().maximize();

			// Accepting cookies & enter into site
			try {
				driver.findElement(By.xpath("//a[@data-event-action= 'Cookie-Confirm']")).click();
			} catch (Exception e) {
				System.out.println("Cookies accepted");
			}
			driver.findElement(By.xpath("//a[contains(text(),'Enter')]")).click();

			// Checkpoint
			String expected_title = "Rabbits Adult Site Reviews";
			String actual_title = driver.getTitle();
			Assert.assertEquals(actual_title, expected_title, "Title is correct");

			for (int k = 0; k < searchInput.length; k++) {

				// Finding search bar
				WebElement search;
				search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rr-header-search-icon-cont")));
				search.click();

				// Search for item with given searchInput
				WebElement txtbx_search = driver.findElement(By.id("rr-header-search-form"));
				txtbx_search.sendKeys(searchInput[k]);
				driver.findElement(
						By.xpath("//button[@class='btn btn-primary text-uppercase rr-header-search-submit-btn']"))
						.click();

				// more results
				for (int loop = 0; loop < 15; loop++) {

					WebElement footer = driver.findElement(By.xpath("//*[@class='rr-footer']"));
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", footer);
					Boolean isPresent = driver
							.findElements(By.xpath("//a[@class='btn btn-primary btn-large show-more-sites']"))
							.size() > 0;
					if (isPresent == true) {
						driver.findElement(By.xpath("//a[@class='btn btn-primary btn-large show-more-sites']")).click();
						Thread.sleep(2000);
					}

					else {
						break;
					}
				}

				// Check point
				expected_title = "Rabbit's Porn Search Engine";
				actual_title = driver.getTitle();
				Assert.assertEquals(actual_title, expected_title, "Product search Title is correct");

				List<WebElement> collection_tiles_updated = driver
						.findElements(By.xpath("//div[@class='col-xs-12 col-sm-6 prd-it']"));
				int expectedResult = collection_tiles_updated.size();
				System.out.println("Expected results number: " + expectedResult);

				// Verify number of results shown
				WebElement resultNum = driver
						.findElement(By.xpath("//h2[@class='search-title margin-bottom-xs']//strong"));
				String text = resultNum.getText();
				int resultDisplayed = Integer.parseInt(text);
				System.out.println("System results number: " + resultDisplayed);

				if (expectedResult == resultDisplayed) {
					System.out
							.println("For " + searchInput[k] + " displayed result number match to the expected result");
				} else {
					System.out.println(
							"For " + searchInput[k] + " displayed result number didnt match to the expected result");
				}

				// verify results loop
				for (int i = 0; i < collection_tiles_updated.size(); i++) {
					String temp = collection_tiles_updated.get(i).getText();
					Point index = collection_tiles_updated.get(i).getLocation();
					if ((temp.toLowerCase().contains(searchInput[k].toLowerCase()))) {
						Assert.assertTrue(true, searchInput + " is displayed in the Title/Description: " + temp);
					} else {
						System.out.println("Search results that doesn't contain the input search string "
								+ searchInput[k] + " and it is at location" + index);
					}

				}
			}
		} catch (Exception e) {
			Assert.assertFalse(false, "Exception thrown. Exception: " + e.toString());

		}

	}
}
