package team.three.usedstroller.collector.service;

import static team.three.usedstroller.collector.validation.PidDuplicationValidator.isNotExistPid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HelloMarketService {

  private final ProductRepository productRepository;

  public int collectingHelloMarket() throws JSONException, InterruptedException {
    int complete = 0;
    int i = 1;
    String url = "https://hellomarket.com/api/search/items?q=유모차&page=" + i + "&limit=30";
    int totalCount = getTotalCount(url);
    int page = totalCount / 30;

    for (i = 1; i <= page; i++) {
      complete += saveByPage(url);
      Thread.sleep(1000);
    }
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
    int cnt = Integer.parseInt(totalCount);
    return cnt;
  }

  private int saveByPage(String url) throws JSONException {
    List<Product> productList = new ArrayList<>();
    JSONObject obj;

    String response = callApi(url);
    JSONObject jsonObject = new JSONObject(response);
    JSONArray jsonArr = jsonObject.optJSONArray("list");

    for (int i = 0; i < jsonArr.length(); i++) {
      obj = (JSONObject) jsonArr.opt(i);
      Product product = covertJSONObjectToProduct(obj);
      productList.add(product);
    }
    int complete = saveItems(productList);

    return complete;
  }

  private static String callApi(String url) {
    return WebClient.create()
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  private Product covertJSONObjectToProduct(JSONObject element) throws JSONException {
    String pid = element.getString("itemIdx");
    String title = element.getString("title");
    String price = element.getString("price");
    String link =
        "https://www.hellomarket.com/item/" + pid;
    String img = element.getString("imageUrl");
    String timestamp = element.getString("timestamp"); //LocalDateTime으로변환필요

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    Date date = new Date(Long.parseLong(timestamp));
    String uploadTime = sdf.format(date);

    return Product.createHelloMarket(pid, title, link, price, img, uploadTime);
  }

  @Transactional
  public int saveItems(List<Product> list) {
    productRepository.saveAll(list);
    return list.size();
  }
}
