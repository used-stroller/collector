package team.three.usedstroller.collector;

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
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

class NaverTest {

	ChromeDriver driver;
	WebDriverWait driverWait;

	@BeforeEach
	void setUp() throws IOException {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*"); // 크로스 도메인 허용
//		options.setHeadless(true);
		driver = new ChromeDriver(options);
		driverWait = new WebDriverWait(driver, Duration.ofSeconds(5));
	}



	@Test
	void crawling_naver() throws InterruptedException {
		String url = "https://search.shopping.naver.com/search/all" +
				"?brand=27112%20215978%2029436%20215480%2026213%20219842%2028497%2013770%20236955%20151538%20242564%2028546" +
				"&frm=NVSHBRD&origQuery=%EC%9C%A0%EB%AA%A8%EC%B0%A8" +
				"&pagingSize=40&productSet=total&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8&sort=rel&timestamp=&viewType=list" +
				"&pagingIndex=1";

		driver.get(url);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		scrollToTheBottomToSeeAllProducts();

		try {
			boolean isNextPage = true;
			int page = 1;
			while (isNextPage) {
				List<WebElement> products = getProducts();
				boolean result = crawlProducts(products);
				System.out.println("page = " + page + ", result = " + result);
				if (result) {
					page++;
					isNextPage = getNextPage();
					Thread.sleep(5000);
					scrollToTheBottomToSeeAllProducts();
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.close();
			driver.quit();
		}
	}

	private List<WebElement> getProducts() {
		WebElement prodList = driver.findElement(By.cssSelector("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx"));
		return prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));
	}

	private boolean crawlProducts(List<WebElement> list) throws InterruptedException {
		String title = "";
		String link = "";
		String price = "";
		String imgSrc = "";

		for (WebElement el : list) {
			link = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]")).getAttribute("href");
			imgSrc = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]/img")).getAttribute("src");
			title = el.findElement(By.xpath(".//a[@title]")).getText();
			price = el.findElement(By.xpath(".//span[contains(@class, 'price')]")).getText()
					.replace("최저", "").trim();
			System.out.println("link = " + link + ", imgSrc = " + imgSrc + ", title = " + title + ", price = " + price);

		}

		Thread.sleep(2000);
		return true;
	}

	private boolean getNextPage() throws InterruptedException {
		WebElement next = driver.findElement(By.xpath("//a[contains(@class, 'pagination_next')]"));
		if (next == null) {
			return false;
		}
		next.click();
		return true;
	}

	private void scrollToTheBottomToSeeAllProducts() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		long intialLength = (long) js.executeScript("return document.body.scrollHeight");

		while (true) {
			js.executeScript("window.scrollTo({top:document.body.scrollHeight, behavior:'smooth'})");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long currentLength = (long) js.executeScript("return document.body.scrollHeight");
			if(intialLength == currentLength) {
				break;
			}
			intialLength = currentLength;
		}
	}

}
