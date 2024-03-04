package team.three.usedstroller.collector.service;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;

@Service
@Slf4j
public class NaverService extends CommonService {

	private final ChromiumDriver driver;

	public NaverService(ProductRepository productRepository, ChromiumDriver driver) {
		super(productRepository);
		this.driver = driver;
	}

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

	@Transactional
	public void start(Integer startPage, Integer endPage) {
		log.info("naver shopping collector start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Integer count = collecting(startPage, endPage);
		stopWatch.stop();
		log.info("네이버쇼핑 완료: {}, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
		super.deleteOldData(SourceType.NAVER);
	}

	public Integer collecting(int startPage, int endPage) {
		driver.open(url + startPage);
		int page = startPage;
		AtomicInteger updateCount = new AtomicInteger(0);

		try {
			while (true) {
				scrollToTheBottomToSeeAllProducts();
				driver.wait(1000);
				List<WebElement> products = getProducts();
				List<Product> items = saveNaverShopping(products);
				int finalPage = page;
				saveProducts(items)
						.doOnSuccess(count -> {
							log.info("naver shopping page: [{}], saved item: [{}], total update: [{}]", finalPage, count, updateCount.addAndGet(count));
						})
						.subscribe();
				if (page == endPage || !getNextPage()) {
					break;
				}
				page++;
				driver.wait(2000);
			}

		} catch (Exception e) {
			throw new IllegalArgumentException("Naver collect error!", e);
		}
		return updateCount.get();
	}

	private List<WebElement> getProducts() {
		scrollToTheBottomToSeeAllProducts();
		WebElement prodList = driver.findOneByXpath("//*[@id=\"content\"]/div[contains(@class, 'content')]/div[contains(@class, 'basicList')]");
		return prodList.findElements(By.xpath(".//div[contains(@class, 'item')]"));
	}

	public List<Product> saveNaverShopping(List<WebElement> list) {
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

		return items;
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
