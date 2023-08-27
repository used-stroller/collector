package team.three.usedstroller.collector.config;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Slf4j
public abstract class BrowserDriver<T extends ChromeDriver> {
	public T driver;
	public WebDriverWait driverWait;
	public ChromeOptions options;

	/**
	 * 페이지 열기
	 */
	public void open(String url) {
		try {
			log.info("Chrome Open URL : {}", url);
			this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			this.driver.get(url);
			this.driver.manage().window().maximize();
		} catch (Exception e) {
			throw new RuntimeException("Chrome Open Error", e);
		}
	}

	/**
	 * Selector가 로드 됐을 때 불러오기
	 */
	public WebElement get(String selector) {
		WebElement element = null;
		try {
			element = driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
		} catch (WebDriverException e) {
			log.error("{} 오브젝트를 불러오는데 실패했습니다.", selector);
		}
		return element;
	}

	/**
	 * Selector가 로드 됐을 때 리스트로 불러오기
	 */
	public List<WebElement> getList(String selector) {
		List<WebElement> elements = null;
		try {
			elements = driverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(selector)));
		} catch (WebDriverException e) {
			log.error("{} 오브젝트를 불러오는데 실패했습니다.", selector);
		}
		return elements;
	}

	/**
	 * Xpath가 로드 됐을 때 불러오기
	 */
	public WebElement getXpath(String selector) {
		WebElement element = null;
		try {
			element = driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selector)));
		} catch (WebDriverException e) {
			log.error("{} 오브젝트를 불러오는데 실패했습니다.", selector);
		}
		return element;
	}

	public List<WebElement> getListXpath(String selector) {
		List<WebElement> element = null;
		try {
			element = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(selector)));
		} catch (WebDriverException e) {
			log.error("{} 오브젝트를 불러오는데 실패했습니다.", selector);
		}
		return element;
	}

	/**
	 * Xpath가 로드 됐을 때 리스트로 불러오기
	 */
	public List<WebElement> getListXpath(String parent, String child) {
		List<WebElement> elements = null;
		try {
			elements = driverWait.until(ExpectedConditions.presenceOfNestedElementsLocatedBy(By.xpath(parent), By.xpath(child)));
		} catch (WebDriverException e) {
			log.error("{} > {} 오브젝트를 불러오는데 실패했습니다.", parent, child);
		}
		return elements;
	}

	/**
	 * driver close
	 */
	public void close() {
		if (driver != null) {
			driver.close();
		}
	}

	/**
	 * driver quit
	 */
	public void quit() {
		if (driver != null) {
			driver.quit();
		}
	}

	/**
	 * 암묵적 대기 설정: 지정 시간동안 대기하며, 요소가 나타나면 즉시 진행
	 */
	public void implicitWait(Duration duration) {
		driver.manage().timeouts().implicitlyWait(duration);
	}


	/**
	 * 일정 시간 대기
	 */
	public void wait(int second) {
		try {
			Thread.sleep(second * 1000L);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	public void scrollY(int y) {
		try {
			driver.executeScript("window.scrollTo(0, "+y+")");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
}
