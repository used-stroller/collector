package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HelloMarketService {

	private final ChromiumDriver driver;
	private final ProductRepository productRepository;

	public int collectingHelloMarket() {
		int complete = 0;
		String url = "https://www.hellomarket.com/search?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
		driver.open(url);
		scrollToTheBottom();

		List<WebElement> list = driver.findAllByXpath("//*[@id=\"__next\"]/div[3]/div[3]/div[2]/div/div/div");
		if (ObjectUtils.isEmpty(list)) {
			return complete;
		}

		int size = saveItems(list);
		complete += size;
		log.info("hello market saved item: {}", complete);

		return complete;
	}

	@Transactional
	public int saveItems(List<WebElement> list) {
		List<Product> items = new ArrayList<>();
		String img;
		String price;
		String title;
		String link;
		String uploadTime;

		for (WebElement element : list) {
			// 광고 제외
			if (!ObjectUtils.isEmpty(element.getAttribute("class"))) {
				continue;
			}

			WebElement textBox = element.findElement(By.xpath(".//div[contains(@class, 'Item__TextBox') or contains(@class, 'Search__TextBox')]"));
			price = textBox.findElement(By.xpath(".//a[1]/div[contains(@class, 'Item__Text')]")).getText();
			title = textBox.findElement(By.xpath(".//a[2]/div[contains(@class, 'Item__Text')]")).getText();
			link = element.findElement(By.xpath(".//a[1]")).getAttribute("href");
			img = element.findElement(By.xpath(".//div[contains(@class, 'Item__ThumbnailBox')]/a/img")).getAttribute("src");
			uploadTime = element.findElement(By.xpath(".//div[contains(@class, 'Item__TimeTag')]")).getText();

			Product product = Product.createHelloMarket(title, link, price, img, uploadTime);
			items.add(product);
		}

		productRepository.saveAll(items);
		return items.size();
	}

	private void scrollToTheBottom() {
		JavascriptExecutor js = (JavascriptExecutor) driver.driver;

		long scrollHeight = 0;
		long afterHeight = 1;

		while (scrollHeight != afterHeight) {
			scrollHeight = (long) js.executeScript("return document.body.scrollHeight"); //현재높이
			WebElement body = driver.findOneByTag("body");
			body.sendKeys(Keys.END);
			driver.wait(2000);
			afterHeight = (long) js.executeScript("return document.body.scrollHeight");
		}
	}


}
