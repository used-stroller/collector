package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
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
		driver.wait(1);
		driver.scrollY(1500);
		driver.wait(1);
		driver.scrollY(1500);
		driver.wait(1);
		driver.scrollY(1500);
		driver.wait(1);
		driver.scrollY(1500);
		driver.wait(1);
		driver.scrollY(1500);
		driver.wait(3);


		try {
			String title = "";
			String link = "";
			String price = "";
			String imgSrc = "";

			WebElement prodList = driver.get("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx");
			List<WebElement> list = prodList.findElements(
					By.xpath(".//div[@class='adProduct_item__1zC9h' or @class='product_item__MDtDF']"));
//			List<WebElement> list = prodList.findElements(By.cssSelector("div.basicList_item__2XT81"));

			for (int i = 0; i < list.size(); i++) {
				//*[@id=\"content\"]/div[1]/div[3]/div/div[1]/div
				link = list.get(i).findElement(By.xpath(".//a[contains(@class, 'thumbnail')]")).getAttribute("href");
				imgSrc = list.get(i).findElement(By.xpath(".//a[contains(@class, 'thumbnail')]/img")).getAttribute("src");
				title = list.get(i).findElement(By.xpath(".//a[@title]")).getText();
				price = list.get(i).findElement(By.xpath(".//span[contains(@class, 'price')]")).getText().replace("최저", "");
				saveNaverShopping(title, link, price, imgSrc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.close();
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
