package team.three.usedstroller.collector.service.reactive;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.dto.SecondWearItem;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.util.SlackHook;

@Service
@Slf4j
public class SecondWearService extends CommonService {

  private final String URL_PATTERN = "https://hellomarket.com/api/search/items?q=유모차&page=%d&limit=%d";
  private final SlackHook slackHook;

  public SecondWearService(ProductRepository productRepository,
      ApplicationEventPublisher eventPublisher, SlackHook slackHook) {
    super(productRepository, eventPublisher);
    this.slackHook = slackHook;
  }

  private static String callApi(String url) {
    return WebClient.create()
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  public void start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer count = collecting();
    stopWatch.stop();
    log.info("세컨웨어 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("세컨웨어", count, stopWatch.getTotalTimeSeconds());
    super.deleteOldData(SourceType.SECOND);
  }

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
          .subscribe(count -> log.info(
              "Secondwear market page: [{}], saved item: [{}], total update: [{}]",
              finalPage, count, updateCount.addAndGet(count)));
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

  private Product covertJSONObjectToProduct(JSONObject element) throws JSONException {

    String pid = element.getString("itemIdx");
    String title = element.getString("title");
    String price = element.getString("price");
    String link =
        "https://www.hellomarket.com/item/" + pid;
    String img = element.getString("imageUrl");
    String timestamp = element.getString("timestamp");

    SecondWearItem item = new SecondWearItem(pid, title, price, link, img,
        Long.parseLong(timestamp));
    return Product.createSecondwear(item);
  }

}
