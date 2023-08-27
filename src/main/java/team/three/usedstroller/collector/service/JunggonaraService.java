package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Junggo;
import team.three.usedstroller.collector.repository.JunggonaraRepository;

import java.util.ArrayList;
import java.util.List;

import static team.three.usedstroller.collector.util.UnitConversionUtils.convertToTimeFormat;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JunggonaraService {

	private final ChromiumDriver driver;
	private final JunggonaraRepository junggonaraRepository;

	public int collectingJunggonara() throws InterruptedException {
		int complete = 0;
		int pageTotal = getTotalPageJungGo();
		for (int i = 1; i < pageTotal; i++) {
			String url = "https://web.joongna.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8?page=" + i;
			driver.open(url);
			Thread.sleep(1000);
			try {
				WebElement content = driver.getXpath("//*[@id=\"__next\"]/div/main/div[1]/div[2]/ul");
				List<WebElement> list = content.findElements(By.xpath("li"));
				List<Junggo> itemList = getItemListJunggo(list);

				for (Junggo item : itemList) {
					junggonaraRepository.save(item);
					complete++;
				}
			} catch (Exception e) {
				continue;
			}
		}
		driver.close();
		return complete;
	}

	private static List<Junggo> getItemListJunggo(List<WebElement> list) {
		List<Junggo> junggoList = new ArrayList<>();
		String img;
		String price;
		String title;
		String link;
		String address = "";
		String uploadTime;
		for (WebElement element : list) {
			try {
				if (element.findElement(By.xpath("a/div[2]/h2")).getText() == null) {
				}
			} catch (Exception e) {
				continue;
			}
			title = element.findElement(By.xpath("a/div[2]/h2")).getText();
			link = element.findElement(By.xpath("a")).getAttribute("href");
			price = element.findElement(By.xpath("a/div[2]/div[1]")).getText();
			img = element.findElement(By.xpath("a/div[1]/img")).getAttribute("src");
			address = element.findElement(By.xpath("a/div[2]/div[2]/span[1]")).getText();
			String time = element.findElement(By.xpath("a/div[2]/div[2]/span[3]")).getText();
			uploadTime = convertToTimeFormat(time);

			Junggo junggo = Junggo.builder()
					.title(title)
					.link(link)
					.price(price)
					.imgSrc(img)
					.address(address)
					.uploadTime(uploadTime)
					.build();
			junggoList.add(junggo);
		}
		return junggoList;
	}

	private int getTotalPageJungGo() {
		int qtyPerPage = 100;
		String url = "https://web.joongna.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8?page=1";
		driver.open(url);
		WebElement content = driver.getXpath("//*[@id=\"__next\"]");
		String totalQty = content.findElement(By.xpath("//*[@id=\"__next\"]/div/main/div[1]/div[2]/div[2]/div/div/div[1]")).getText();
		String intStr = totalQty.replaceAll("[^0-9]", "");
		int totalQtyInt = Integer.parseInt(intStr);
		return totalQtyInt / qtyPerPage;
	}

}
