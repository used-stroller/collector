package team.three.usedstroller.collector.service;

import static team.three.usedstroller.collector.validation.PidDuplicationValidator.isNotExistPid;

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
		String link = "";
		String uploadTime;

		for (WebElement element : list) {
			WebElement textBox = element.findElement(By.xpath(".//div/div[2]"));
			img = textBox.findElement(By.xpath(".//img")).getAttribute("src");
			System.out.println("img = " + img);

			System.out.println("element = " + element.getAttribute("class"));
			System.out.println("element = " + element.getTagName());
			System.out.println("element = " + element.getText());
			price = textBox.findElement(By.xpath(".//div[2]")).getText();
			title = textBox.findElement(By.xpath(".//div[3]")).getText();
			uploadTime = textBox.findElement(By.xpath(".//div[4]")).getText();
			if (uploadTime.contains("무료배송")) {
				uploadTime = textBox.findElement(By.xpath(".//div[5]")).getText();
			}
//			link = element.findElement(By.xpath(".//a[1]")).getAttribute("href");
//			img = element.findElement(By.xpath(".//div/div[1]/div[1]/img")).getAttribute("src");
//			uploadTime = element.findElement(By.xpath(".//div[contains(@class, 'Item__TimeTag')]")).getText();

			Product product = Product.createHelloMarket(title, link, price, img, uploadTime);
			if (isNotExistPid(productRepository, product)) {
				items.add(product);
			}
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
