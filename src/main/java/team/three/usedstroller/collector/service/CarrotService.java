package team.three.usedstroller.collector.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
public class CarrotService extends CommonService {

  private final String url = UriComponentsBuilder.newInstance()
      .scheme("https")
      .host("www.daangn.com")
      .path("/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market")
      .queryParam("next_page", "")
      .build().toUriString();
  private final SlackHook slackHook;

  public CarrotService(ProductRepository productRepository,
      ApplicationEventPublisher eventPublisher, SlackHook slackHook) {
    super(productRepository, eventPublisher);
    this.slackHook = slackHook;
  }

  public void start(Integer startPage, Integer endPage) {
    log.info("carrot market collector start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer count = collecting(startPage, endPage);
    stopWatch.stop();
    log.info("당근 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("당근", count, stopWatch.getTotalTimeSeconds());
    super.deleteOldData(SourceType.CARROT);
  }

  public Integer collecting(Integer startPage, Integer endPage) {
    AtomicInteger updateCount = new AtomicInteger(0);

    for (int page = startPage; page <= endPage; page++) {
      try {
        List<Product> carrots = crawlingCarrotPage(url + page);
        if (ObjectUtils.isEmpty(carrots)) {
          log.info("carrot market page: [{}] is empty", page);
          break;
        }
        int finalPage = page;
        saveProducts(carrots)
            .doOnSuccess(count -> {
              log.info("carrot market page: [{}], saved item: [{}], total update: [{}]", finalPage,
                  count, updateCount.addAndGet(count));
            })
            .subscribe();
      } catch (Exception e) {
        throw new IllegalArgumentException("carrot market connect error", e);
      }
    }

    return updateCount.get();
  }

  private List<Product> crawlingCarrotPage(String url) throws IOException {
    List<Product> items = new ArrayList<>();
    Document doc = Jsoup.connect(url).get();
    doc.select("article.flea-market-article")
        .forEach(element -> {
          String title = element.select("span.article-title").text();
          String content = element.select("span.article-content").text();
          String region = element.select("p.article-region-name").text();
          String price = element.select("p.article-price").text();
          String imgSrc = element.select("div.card-photo > img").attr("src");
          String link = element.select("a.flea-market-article-link").attr("href");
          Document detailDoc = null;
          String uploadTime = "";
          try {
            detailDoc = Jsoup.connect("https://www.daangn.com" + link).get();
            Element time = detailDoc.getElementsByTag("time").stream().findFirst()
                .orElseGet(() -> null);
            uploadTime = ObjectUtils.isEmpty(time) ? "" : time.text().replace("끌올", "");
          } catch (IOException e) {
            throw new IllegalArgumentException("당근마켓 상세정보 가져오기 실패", e);
          }
          Product product = Product.createCarrot(title, price, region, link, imgSrc, content,
              uploadTime);
          items.add(product);
        });
    return items;
  }

}
