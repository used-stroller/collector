package team.three.usedstroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import team.three.usedstroller.collector.domain.NaverShopping;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
	void crawling_naver() throws InterruptedException {
		String url = "https://search.shopping.naver.com/search/all?where=all&frm=NVSCTAB&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
		driver.get(url);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		long intialLength = (long) js.executeScript("return document.body.scrollHeight");

		while (true) {
			js.executeScript("window.scrollTo({top:document.body.scrollHeight, behavior:'smooth'})");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long currentLength = (long) js.executeScript("return document.body.scrollHeight");
			if(intialLength == currentLength) {
				break;
			}
			intialLength = currentLength;
		}


		try {
			String link = "";
			String imgSrc = "";
			String title = "";
			String price = "";

//			List<WebElement> list = driver.getListXpath("/html/body/div[1]/div/div[2]/div[2]/div[3]/div[1]/div[3]/div",
//					"./div");
			WebElement prodList = driverWait.until(
							ExpectedConditions.presenceOfElementLocated(
									By.cssSelector("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx > div")));
			List<WebElement> list1 = prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));
			List<WebElement> list = driverWait.until(ExpectedConditions.visibilityOfAllElements(list1));
//					By.xpath("//div[@class='adProduct_item__1zC9h' or @class='product_item__MDtDF']")));

			for (int i = 0; i < list.size(); i++) {
				//*[@id=\"content\"]/div[1]/div[3]/div/div[1]/div
				link = list.get(i).findElement(By.xpath(".//a[contains(@class, 'thumbnail')]")).getAttribute("href");
				imgSrc = list.get(i).findElement(By.xpath(".//a[contains(@class, 'thumbnail')]/img")).getAttribute("src");
				title = list.get(i).findElement(By.xpath(".//a[@title]")).getText();
				price = list.get(i).findElement(By.xpath(".//span[contains(@class, 'price')]")).getText().replace("최저", "");
				NaverShopping naver = NaverShopping.builder()
						.title(title)
						.link(link)
						.price(price)
						.imgSrc(imgSrc)
						.build();
				System.out.println("naver = " + naver.toString());
			}
//			for (WebElement el : list) {
//				System.out.println("link = " + el.findElement(By.xpath(".//a[contains(@class, 'thumb')]")).getAttribute("href"));
//				System.out.println("image = " + el.findElement(By.xpath(".//a[contains(@class, 'thumb')]"))
//						.findElement(By.xpath("//img[contains(@width, '140')]")).getAttribute("src"));
//				System.out.println("title = " + el.findElement(By.xpath(".//a[@title]")).getText());
//				System.out.println("price = " + el.findElement(By.xpath(".//span[contains(@class, 'price')]")).getText());
//			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.close();
			driver.quit();
		}
	}

}
