package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectorService {

	private final ChromiumDriver driver;

	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver"; // 드라이버 ID
	public static final String WEB_DRIVER_PATH = "C:/chromedriver/chromedriver.exe"; // 드라이버 경로

	public void collectingNaver(String url) {
		driver.open(url);
		driver.wait(1);

		WebElement americaIndex = driver.get("#americaIndex");
		List<WebElement> list = americaIndex.findElements(By.className("point_dn"));
		for (WebElement webElement : list) {
			log.info("title = {}", webElement.findElement(By.cssSelector(".tb_td2")).getText());
			log.info("price = {}", webElement.findElement(By.cssSelector(".tb_td3")).getText());
			log.info("rate = {}", webElement.findElement(By.cssSelector(".tb_td5")).getText());
		}

		driver.close();
	}
	public void collectingCoopang(String url) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");

		WebDriver webDriver = new ChromeDriver(options);
		webDriver.get(url);
		System.setProperty(WEB_DRIVER_ID,WEB_DRIVER_PATH);
		WebElement searchBox = webDriver.findElement(By.id("q"));
		System.out.println("searchBox = " + searchBox);
		searchBox.sendKeys("유모차");
		searchBox.sendKeys(Keys.ENTER);
/*		if(webDriver !=null){
			webDriver.close();
			webDriver.quit();
		}*/
	}


}
