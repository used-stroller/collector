package team.three.usedstroller.collector.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class HelloMarketService {

  private final ChromiumDriver driver;
  private final ProductRepository productRepository;

  public int collectingHelloMarket() throws JSONException {
    int complete = 0;
    int i = 1;
    String url = "https://hellomarket.com/api/search/items?q=유모차&page=" + i
        + "&limit=30";
    getTotalCount(url);

/*
    int size = saveItems(list);
    complete += size;*/
    log.info("hello market saved item: {}", complete);

    return complete;
  }

  private int getTotalCount(String url) throws JSONException {
    String response = WebClient.create()
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    JSONObject jsonObject = new JSONObject(response);
    String totalCount = jsonObject.getJSONObject("result").getString("totalCount");
    System.out.println("totalCount = " + totalCount);

    return 1;
  }

  @Transactional
  public int saveItems(List<WebElement> list) {
    List<Product> items = new ArrayList<>();
    String img;
    String price;
    String title;
    String link = "";
    String uploadTime;
    for (int i = 1; i <= list.size(); i++) {
      WebElement element = driver.findOneByXpath(
          "//*[@id=\"__next\"]/div[3]/div[3]/div[2]/div/div/div[" + i + "]");
      try {

        img = element.findElement(By.xpath("div/div[1]/div[1]/img")).getAttribute("src");
        System.out.println("img = " + img);

        System.out.println("element = " + element.getAttribute("class"));
        System.out.println("element = " + element.getTagName());
        System.out.println("element = " + element.getText());
        price = element.findElement(By.xpath("div/div[2]/div[2]")).getText();
        title = element.findElement(By.xpath("div/div[2]/div[3]")).getText();
        uploadTime = element.findElement(By.xpath("div/div[2]/div[4]")).getText();
        if (uploadTime.contains("무료배송")) {
          uploadTime = element.findElement(By.xpath("div/div[2]/div[5]")).getText();
        }
        driver.back();
        driver.wait(2000);
      } catch (Exception e) {
        continue;
      }

      Product product = Product.createHelloMarket(title, link, price, img, uploadTime);
      items.add(product);
    }

    productRepository.saveAll(items);
    return items.size();
  }


}
