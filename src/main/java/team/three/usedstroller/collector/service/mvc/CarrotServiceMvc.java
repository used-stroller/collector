package team.three.usedstroller.collector.service.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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
import team.three.usedstroller.collector.service.ProductCollector;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarrotServiceMvc implements ProductCollector {

  private final ProductRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final SlackHook slackHook;
  private final Integer END_PAGE = 3000;

  private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
      .scheme("https")
      .host("www.daangn.com")
      .path("/search/유모차/more/flea_market")
      .queryParam("next_page", "")
      .encode();
  //https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market?next_page=1500

  @Override
  public void start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer newProductsCount = collectProduct();
    stopWatch.stop();
    log.info("당근 완료: {}건, 수집 시간: {}s", newProductsCount, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("당근", newProductsCount, stopWatch.getTotalTimeSeconds());
    deleteOldProducts(SourceType.CARROT);
  }

  @Override
  public Integer collectProduct() {
    AtomicInteger updateCount = new AtomicInteger(0);

    IntStream.rangeClosed(1, END_PAGE)
        .forEach(page -> {
          String url = uriBuilder
              .replaceQueryParam("next_page", page)
              .build()
              .toUriString();

          log.info("carrot market page: [{}] start", page);
          List<Product> products = getProducts(url);
          if (ObjectUtils.isEmpty(products)) {
            log.info("carrot market page: [{}] is empty", page);
            return;
          }
          updateCount.addAndGet(saveProducts(repository, products));
        });

    return updateCount.get();
  }

  @Override
  public void deleteOldProducts(SourceType sourceType) {
    eventPublisher.publishEvent(sourceType);
  }

  private List<Product> getProducts(String url) {
    List<Product> items = new ArrayList<>();
    Document doc = null;
    try {
      doc = Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new IllegalArgumentException("Carrot jsoup connect fail", e);
    }
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
            // 페이지 요청 실패 시 건너뜀
            log.error("당근마켓 상세정보 가져오기 실패 URL: {}", url, e);
          }
          Product product = Product.createCarrot(title, price, region, link, imgSrc, content,
              uploadTime);
          items.add(product);
        });
    return items;
  }
}
