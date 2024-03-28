package team.three.usedstroller.collector.service.mvc;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.dto.NaverApiResponse;
import team.three.usedstroller.collector.domain.dto.NaverApiResponse.Items;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.ProductCollector;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverServiceMvc implements ProductCollector {

  private final ProductRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final RestTemplate restTemplate;
  private final SlackHook slackHook;
  private final Environment environment;
  private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
      .scheme("https")
      .host("openapi.naver.com")
      .path("/v1/search/shop.json")
      .queryParam("query", "유모차")
      .queryParam("display", 100)
      .queryParam("sort", "sim")
      .queryParam("filter", "")
      .queryParam("exclude", "rental:cbshop")
      .queryParam("start", "")
      .encode();
  //  @Value("${naver.id}")
  private String id;
  //  @Value("${naver.secret}")
  private String secret;

  @PostConstruct
  public void init() {
    this.id = environment.getProperty("naver.id");
    this.secret = environment.getProperty("naver.secret");
  }

  @Override
  public void start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer newProductsCount = collectProduct();
    stopWatch.stop();
    log.info("네이버 완료: {}건, 수집 시간: {}s", newProductsCount, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("네이버", newProductsCount, stopWatch.getTotalTimeSeconds());
    deleteOldProducts(SourceType.NAVER);
  }

  @Override
  public Integer collectProduct() {
    AtomicInteger updateCount = new AtomicInteger(0);

    IntStream.rangeClosed(0, 9)
        .forEach(page -> {
          URI uri = uriBuilder
              .replaceQueryParam("start", page * 100 + 1)
              .build()
              .toUri();
          Map<String, String> requestHeaders = new HashMap<>();
          requestHeaders.put("X-Naver-Client-Id", id);
          requestHeaders.put("X-Naver-Client-Secret", secret);
          HttpHeaders headers = new HttpHeaders();
          headers.setAll(requestHeaders);

          ResponseEntity<NaverApiResponse> response = restTemplate.exchange(uri,
              HttpMethod.GET, new HttpEntity<>(headers), NaverApiResponse.class);
          if (ObjectUtils.isEmpty(response.getBody())) {
            log.info("naver api response is empty. page: {}", page);
            return;
          }
          List<Product> products = convertProducts(response.getBody().getItems());
          Integer saved = saveProducts(repository, products);
          log.info("naver page: [{}], saved item: [{}], total update: [{}]", page, saved,
              updateCount.addAndGet(saved));
        });

    return updateCount.get();
  }

  @Override
  public void deleteOldProducts(SourceType sourceType) {
    eventPublisher.publishEvent(sourceType);
  }

  private List<Product> convertProducts(List<Items> items) {
    return items.stream()
        .map(Product::createNaver)
        .toList();
  }
}
