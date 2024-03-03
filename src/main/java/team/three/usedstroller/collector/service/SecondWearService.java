package team.three.usedstroller.collector.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

@Service
@Slf4j
public class SecondWearService extends CommonService {

  public SecondWearService(ProductRepository productRepository) {
    super(productRepository);
  }

  private final String URL_PATTERN = "https://hellomarket.com/api/search/items?q=유모차&page=%d&limit=%d";

  public Integer collecting() {
    AtomicInteger updateCount = new AtomicInteger(0);
    int totalPage = getTotalPage();

    for (int page = 1; page <= totalPage; page++) {
      List<Product> products = collectByPage(page);
      if (ObjectUtils.isEmpty(products)) {
        log.info("Secondwear page: [{}] is empty", page);
        break;
      }
      int finalPage = page;
      saveProducts(products)
          .subscribe(count -> {
            log.info("Secondwear market page: [{}], saved item: [{}]", finalPage, count);
            updateCount.addAndGet(count);
          });
    }

    return updateCount.get();
  }

  private int getTotalPage() {
    String url = String.format(URL_PATTERN, 1, 1); // page=1, limit=1

    String response = WebClient.create()
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    String totalCount = "";
    try {
      JSONObject jsonObject = new JSONObject(response);
      totalCount = jsonObject.getJSONObject("result").getString("totalCount");
    } catch (JSONException e) {
      throw new IllegalArgumentException("Secondwear Json error", e);
    }
    return (Integer.parseInt(totalCount) / 30) + 1;
  }

  private List<Product> collectByPage(int page) {
    String url = String.format(URL_PATTERN, page, 30); // limit=30

    List<Product> productList = new ArrayList<>();
    JSONObject obj;
    String response = callApi(url);

    try {
      JSONObject jsonObject = new JSONObject(response);
      JSONArray jsonArr = jsonObject.optJSONArray("list");

      for (int i = 0; i < jsonArr.length(); i++) {
        obj = (JSONObject) jsonArr.opt(i);
        Product product = covertJSONObjectToProduct(obj);
        productList.add(product);
      }
    } catch (JSONException e) {
      throw new IllegalArgumentException("Secondwear Json error", e);
    }

    return productList;
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

}
