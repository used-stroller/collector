package team.three.usedstroller.collector.service;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static team.three.usedstroller.collector.validation.PidDuplicationValidator.isExistPid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaverService {

	private final ChromiumDriver driver;
	private final ProductRepository productRepository;
	private final String url = UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("search.shopping.naver.com")
			.path("/search/all")
			.queryParam("brand", "27112%20215978%20215480%2029436%2013770%2026213%20236955%20219842%20242564%2028546%2028497%20151538%20134696%20212765%20148890%20242729%20240016")
			.queryParam("frm", "NVSHATC")
			.queryParam("origQuery", "%EC%9C%A0%EB%AA%A8%EC%B0%A8")
			.queryParam("pagingSize", "40")
			.queryParam("productSet", "total")
			.queryParam("query", "%EC%9C%A0%EB%AA%A8%EC%B0%A8")
			.queryParam("sort", "rel")
			.queryParam("timestamp", "")
			.queryParam("viewType", "list")
			.queryParam("pagingIndex", "")
			.build().toUriString();

	public String collectingNaverShopping(int startPage, int endPage) {
		driver.open(url + startPage);
		int page = startPage;

		try {
			while (true) {
				scrollToTheBottomToSeeAllProducts();
				driver.wait(1000);
				List<WebElement> products = getProducts();
				int size = saveNaverShopping(products);
				log.info("naver shopping page: [{}], saved item: [{}]", page, size);
				if (page == endPage || !getNextPage()) {
					break;
				}
				page++;
				driver.wait(2000);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return "naver shopping collector complete. total page: " + page;
	}

	private List<WebElement> getProducts() {
		scrollToTheBottomToSeeAllProducts();
		WebElement prodList = driver.findOneByXpath("//*[@id=\"content\"]/div[contains(@class, 'content')]/div[contains(@class, 'basicList')]");
		return prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));
	}

	@Transactional
	public int saveNaverShopping(List<WebElement> list) {
		List<Product> items = new ArrayList<>();
		String pid = "";
		String title = "";
		String link = "";
		String price = "";
		String imgSrc = "";
		String uploadDate = "";
		String releaseYear = "";
		String etc = "";

		for (WebElement el : list) {
			pid = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail_thumb')]")).getAttribute("data-i");
			if (isExistPid(productRepository, pid, SourceType.NAVER)) {
				continue;
			}
			link = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail_thumb')]")).getAttribute("href");

			try {
				imgSrc = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail_thumb')]/img")).getAttribute("src");
			} catch (NoSuchElementException e) {
				imgSrc = el.findElement(By.xpath(".//a[contains(@class, 'thumbnail_thumb')]/span")).getText(); // 이미지 없는 경우(예: 청소년 유해상품)
			}

			title = el.findElement(By.xpath(".//a[@title]")).getText();

			try {
				price = el.findElement(By.xpath(".//span[contains(@class, 'price')]")).getText()
						.replace("최저", "").trim();
			} catch (NoSuchElementException e) {
				price = el.findElement(By.xpath(".//div[contains(@class, 'price')]")).getText(); // 가격 없는 경우(예: 판매중단)
			}

			uploadDate = el.findElements(By.xpath(".//span[contains(@class, 'product_etc')]"))
					.stream()
					.filter(e -> e.getText() != null && e.getText().contains("등록일"))
					.findFirst()
					.map(e -> e.getText().split("등록일")[1].trim())
					.orElseGet(() -> EMPTY_STRING);

			releaseYear = el.findElements(By.xpath(".//a[contains(@class, 'product_detail')]"))
					.stream()
					.filter(e -> e.getText() != null && e.getText().contains("출시년도"))
					.findFirst()
					.map(e -> e.getText().split("출시년도 :")[1].trim())
					.orElseGet(() -> EMPTY_STRING);

			etc = el.findElements(By.xpath(".//a[contains(@class, 'product_detail')]"))
					.stream()
					.map(WebElement::getText)
					.filter(e -> !ObjectUtils.isEmpty(e))
					.collect(Collectors.joining(" | "));

			Product product = Product.createNaver(pid, title, link, price, imgSrc, uploadDate, releaseYear, etc);
			items.add(product);
		}

		List<Product> result = productRepository.saveAll(items);
		return result.size();
	}

	private boolean getNextPage() {
		WebElement next = driver.findOneByXpath("//a[contains(@class, 'pagination_next')]");
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
			driver.wait(1000);
			long currentLength = (long) js.executeScript("return document.body.scrollHeight");
			if(intialLength == currentLength) {
				break;
			}
			intialLength = currentLength;
		}
	}

}
