package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
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
public class CollectorService {

	private final ChromiumDriver driver;
	private final NaverShoppingRepository naverShoppingRepository;

	public String collectingNaverShopping(String url, int startPage, int endPage) {
		driver.open(url + startPage);
		driver.implicitWait(10);
		int page = startPage;

		try {
			while (true) {
				scrollToTheBottomToSeeAllProducts();
				driver.wait(1);
				List<WebElement> products = getProducts();
				boolean result = crawlingProducts(products);
				log.info("page = {}, save result = {}", page, result);
				if (result) {
					if (page == endPage || !getNextPage()) {
						break;
					}
					page++;
					driver.wait(5);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "naver shopping collector complete. total page: " + page;
	}

	private List<WebElement> getProducts() {
		scrollToTheBottomToSeeAllProducts();
		WebElement prodList = driver.get("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx");
		return prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));
	}

	@Transactional
	public boolean crawlingProducts(List<WebElement> list) {
		String title = "";
		String link = "";
		String price = "";
		String imgSrc = "";

		for (WebElement el : list) {
			link = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]")).getAttribute("href");

			try {
				imgSrc = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]/img")).getAttribute("src");
			} catch (NoSuchElementException e) {
				imgSrc = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail')]/span")).getText(); // 이미지 없는 경우(예: 청소년 유해상품)
			}

			title = el.findElement(By.xpath(".//a[@title]")).getText();

			try {
				price = el.findElement(By.xpath(".//span[contains(@class, 'price')]")).getText()
						.replace("최저", "").trim();
			} catch (NoSuchElementException e) {
				price = el.findElement(By.xpath(".//div[contains(@class, 'price')]")).getText(); // 가격 없는 경우(예: 판매중단)
			}

			saveNaverShopping(title, link, price, imgSrc);
		}

		driver.wait(2);
		return true;
	}

	private boolean getNextPage() {
		WebElement next = driver.getXpath("//a[contains(@class, 'pagination_next')]");
		if (next == null) {
			return false;
		}
		next.click();
		return true;
	}

	private void scrollToTheBottomToSeeAllProducts() {
		JavascriptExecutor js = driver.driver;
		long intialLength = (long) js.executeScript("return document.body.scrollHeight");

		while (true) {
			js.executeScript("window.scrollTo({top:document.body.scrollHeight, behavior:'smooth'})");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			long currentLength = (long) js.executeScript("return document.body.scrollHeight");
			if(intialLength == currentLength) {
				break;
			}
			intialLength = currentLength;
		}
	}

	private void saveNaverShopping(String title, String link, String price, String imgSrc) {
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
