package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectorService {

	private final ChromiumDriver driver;

	public void collectingNaver(String url) {
		driver.open(url);
		driver.wait(1);

		WebElement americaIndex = driver.get("#americaIndex");
		List<WebElement> list = americaIndex.findElements(By.className("point_dn"));
		for (WebElement webElement : list) {
			log.info("title = {}", webElement.findElement(By.cssSelector(".tb_td2")).getText());
			log.info("price = {}", webElement.findElement(By.cssSelector(".tb_td3")).getText());
			log.info("rate = {}", webElement.findElement(By.cssSelector(".tb_td5")).getText());
		}

		driver.close();
	}


}
