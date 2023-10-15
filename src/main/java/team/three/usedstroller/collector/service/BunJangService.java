package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;

@Service
@Slf4j
@RequiredArgsConstructor
public class BunJangService {

	private final ChromiumDriver driver;
	private final ProductRepository productRepository;

	public int collectingBunJang() {
		int complete = 0;
		int pageTotal = getTotalPageBunJang();
		log.info("bunjang total page: {}", pageTotal);

		for (int i = 1; i <= pageTotal; i++) {
			String url = "https://m.bunjang.co.kr/search/products?order=score&page=" + i + "&q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
			driver.open(url);
			driver.wait(1000);

			WebElement content = driver.findOneByCss("#root");
			List<WebElement> list = content.findElements(By.xpath("div/div/div[4]/div/div[4]/div/div"));
			if (ObjectUtils.isEmpty(list)) {
				return complete;
			}

			int size = saveItemList(list);
			complete += size;
			log.info("bunjang page: [{}], saved item: [{}]", i, complete);
		}

		return complete;
	}

	@Transactional
	public int saveItemList(List<WebElement> list) {
		List<Product> items = new ArrayList<>();
		String img;
		String price;
		String title;
		String link;
		String address;
		String uploadTime;

		for (WebElement element : list) {
			uploadTime = element.findElements(By.xpath("a/div[2]/div[2]/div[2]/span"))
				.stream()
				.findFirst()
				.map(WebElement::getText)
				.orElseGet(() -> EMPTY_STRING);

			if (uploadTime.isEmpty()) {
				continue; // uploadTime이 없으면 광고이기 때문에 패스
			}

			title = element.findElement(By.xpath("a/div[2]/div[1]")).getText();
			link = element.findElement(By.xpath("a")).getAttribute("href");
			price = element.findElement(By.xpath("a/div[2]/div[2]/div[1]")).getText();
			img = element.findElement(By.xpath("a/div[1]/img")).getAttribute("src");
			address = element.findElements(By.xpath("a/div[3]"))
					.stream()
					.findFirst()
					.map(WebElement::getText)
					.orElseGet(() -> EMPTY_STRING);

			Product product = Product.createBunJang(title, link, price, img, address, uploadTime);
			items.add(product);
		}

		productRepository.saveAll(items);
		return items.size();
	}

	private int getTotalPageBunJang() {
		int qtyPerPage = 100;
		String url = "https://m.bunjang.co.kr/search/products?order=score&page=1&q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
		driver.open(url);
		WebElement content = driver.findOneByXpath("//*[@id=\"root\"]");
		String totalQty = content.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[4]/div/div[3]/div/div[1]/span[2]")).getText();
		String intStr = totalQty.replaceAll("[^0-9]", "");
		int totalQtyInt = Integer.parseInt(intStr);
		return (int) Math.ceil((double) totalQtyInt / qtyPerPage);
	}

}
