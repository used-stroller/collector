package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.NaverShopping;
import team.three.usedstroller.collector.repository.NaverShoppingRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverService {

	private final ChromiumDriver driver;
	private final NaverShoppingRepository naverShoppingRepository;

	public String collectingNaverShopping(String url, int startPage, int endPage) {
		driver.open(url + startPage);
		driver.implicitWait(Duration.ofMillis(100));
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
					driver.wait(3);
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
		String uploadDate = "";
		String releaseYear = "";
		String etc = "";

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

			uploadDate = el.findElements(By.xpath(".//span[contains(@class, 'roduct_etc')]"))
					.stream()
					.filter(e -> e.getText() != null && e.getText().contains("등록일"))
					.findFirst()
					.map(e -> e.getText().split("등록일")[1].trim())
					.orElseGet(() -> EMPTY_STRING);

			releaseYear = el.findElements(By.xpath(".//a[contains(@class, 'roduct_detail')]"))
					.stream()
					.filter(e -> e.getText() != null && e.getText().contains("출시년도"))
					.findFirst()
					.map(e -> e.getText().split("출시년도 :")[1].trim())
					.orElseGet(() -> EMPTY_STRING);

			etc = el.findElements(By.xpath(".//a[contains(@class, 'roduct_detail')]"))
					.stream()
					.map(WebElement::getText)
					.filter(e -> !ObjectUtils.isEmpty(e))
					.collect(Collectors.joining(" | "));

			NaverShopping result = NaverShopping.builder()
					.title(title)
					.link(link)
					.price(price)
					.imgSrc(imgSrc)
					.uploadDate(uploadDate)
					.releaseYear(releaseYear)
					.etc(etc)
					.build();

			saveNaverShopping(result);
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

	private void saveNaverShopping(NaverShopping result) {
		NaverShopping save = naverShoppingRepository.save(result);
		log.info("saved id: [{}] {}", save.getId(), save.getTitle());
	}

}
