package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.BunJang;
import team.three.usedstroller.collector.repository.BunJangRepository;

import java.util.ArrayList;
import java.util.List;

import static team.three.usedstroller.collector.util.UnitConversionUtils.convertToTimeFormat;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BunJangService {

	private final ChromiumDriver driver;
	private final BunJangRepository bunJangRepository;

	public int collectingBunJang() throws InterruptedException {
		int complete = 0;
		int pageTotal = getTotalPageBunJang();
		for (int i = 1; i < pageTotal; i++) {
			String url = "https://m.bunjang.co.kr/search/products?order=score&page=" + i + "&q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
			driver.open(url);
			Thread.sleep(1000);

			WebElement content = driver.getSelector("#root");
			List<WebElement> list = content.findElements(By.xpath("div/div/div[4]/div/div[4]/div/div"));

			List<BunJang> itemList = getItemListBunJang(list);
			for (BunJang item : itemList) {
				bunJangRepository.save(item);
				complete++;
			}
		}
		driver.close();
		return complete;
	}

	private static List<BunJang> getItemListBunJang(List<WebElement> list) {
		List<BunJang> bunJangList = new ArrayList<>();
		String img;
		String price;
		String title;
		String link;
		String address;
		String uploadTime;
		for (WebElement element : list) {
			try {
				if (element.findElement(By.xpath("a")).getText() == null) {
				}
			} catch (Exception e) {
				continue;
			}
			title = element.findElement(By.xpath("a/div[2]/div[1]")).getText();
			link = element.findElement(By.xpath("a")).getAttribute("href");
			price = element.findElement(By.xpath("a/div[2]/div[2]/div[1]")).getText();
			img = element.findElement(By.xpath("a/div[1]/img")).getAttribute("src");
			address = element.findElement(By.xpath("a/div[3]")).getText();
			String time = element.findElement(By.xpath("a/div[2]/div[2]/div[2]/span")).getText();
			uploadTime = convertToTimeFormat(time);

			BunJang bunJang = BunJang.builder()
					.title(title)
					.link(link)
					.price(price)
					.imgSrc(img)
					.address(address)
					.uploadTime(uploadTime)
					.build();
			bunJangList.add(bunJang);
		}
		return bunJangList;
	}

	private int getTotalPageBunJang() {
		int qtyPerPage = 100;
		String url = "https://m.bunjang.co.kr/search/products?order=score&page=1&q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
		driver.open(url);
		WebElement content = driver.getXpath("//*[@id=\"root\"]");
		String totalQty = content.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[4]/div/div[3]/div/div[1]/span[2]")).getText();
		String intStr = totalQty.replaceAll("[^0-9]", "");
		int totalQtyInt = Integer.parseInt(intStr);
		return totalQtyInt / qtyPerPage;
	}

}
