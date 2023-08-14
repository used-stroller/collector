package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.NaverShopping;
import team.three.usedstroller.collector.repository.NaverShoppingRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectorService {

	private final ChromiumDriver driver;
	private final NaverShoppingRepository naverShoppingRepository;

	public void collectingNaverShopping(String url) {
		driver.open(url);
		driver.implicitWait(10);
		scrollToTheBottomToSeeAllProducts();

		try {
			String title = "";
			String link = "";
			String price = "";
			String imgSrc = "";

			WebElement prodList = driver.get("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx");
			List<WebElement> list = prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));

			for (WebElement el : list) {
				link = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]")).getAttribute("href");
				imgSrc = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]/img")).getAttribute("src");
				title = el.findElement(By.xpath(".//a[@title]")).getText();
				price = el.findElement(By.xpath(".//span[contains(@class, 'price')]")).getText()
						.replace("최저", "").trim();
				saveNaverShopping(title, link, price, imgSrc);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			driver.close();
		}

	}

	private void scrollToTheBottomToSeeAllProducts() {
		JavascriptExecutor js = (JavascriptExecutor) driver.getDriver();
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
	}

	@Transactional
	public void saveNaverShopping(String title, String link, String price, String imgSrc) {
		NaverShopping result = NaverShopping.builder()
				.title(title)
				.link(link)
				.price(price)
				.imgSrc(imgSrc)
				.build();
		NaverShopping save = naverShoppingRepository.save(result);
		log.info("saved item = {}", save);
	}

}
