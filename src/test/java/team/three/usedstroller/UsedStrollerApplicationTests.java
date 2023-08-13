package team.three.usedstroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

class UsedStrollerApplicationTests {

	ChromeDriver driver;
	WebDriverWait driverWait;

	@BeforeEach
	void setUp() throws IOException {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*"); // 크로스 도메인 허용
//		options.setHeadless(true);
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		driver = new ChromeDriver(options);
		driverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
	}



	@Test
	void crawling_naver() {
		String url = "https://search.shopping.naver.com/search/all?where=all&frm=NVSCTAB&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
		driver.get(url);
		driver.executeScript("window.scrollTo(0, 1000)");
		driver.executeScript("window.scrollTo(1001, 2000)");
		driver.executeScript("window.scrollTo(2001, 3000)");
		driver.executeScript("window.scrollTo(3001, 4000)");
		driver.executeScript("window.scrollTo(4001, 5000)");

		try {

//			List<WebElement> list = driver.getListXpath("/html/body/div[1]/div/div[2]/div[2]/div[3]/div[1]/div[3]/div",
//					"./div");
			WebElement prodList = driverWait.until(
							ExpectedConditions.presenceOfElementLocated(
									By.cssSelector("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx > div")));
			List<WebElement> list = prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));

			for (WebElement el : list) {
				System.out.println("link = " + el.findElement(By.xpath(".//a[contains(@class, 'thumb')]")).getAttribute("href"));
				System.out.println("image = " + el.findElement(By.xpath(".//a[contains(@class, 'thumb')]"))
						.findElement(By.xpath("//img[contains(@width, '140')]")).getAttribute("src"));
				System.out.println("title = " + el.findElement(By.xpath(".//a[@title]")).getText());
				System.out.println("price = " + el.findElement(By.xpath(".//span[contains(@class, 'price')]")).getText());
//				System.out.println("brand" + el.findElement(By.xpath("//div[contains(@class, 'mall_title']")).getText());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.close();
			driver.quit();
		}
	}

}
