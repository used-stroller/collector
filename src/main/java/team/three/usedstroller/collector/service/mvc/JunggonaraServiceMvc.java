package team.three.usedstroller.collector.service.mvc;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
import team.three.usedstroller.collector.domain.dto.JunggonaraApiRequest;
import team.three.usedstroller.collector.domain.dto.JunggonaraApiResponse;
import team.three.usedstroller.collector.domain.dto.JunggonaraItem;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.ProductCollector;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
@RequiredArgsConstructor
public class JunggonaraServiceMvc implements ProductCollector {

  private final ProductRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final RestTemplate restTemplate;
  private final SlackHook slackHook;

  private final String SORT = "RECENT_SORT";
  private final String KEYWORD = "유모차";
  private final Integer QUANTITY = 100;
  private final URI uri = UriComponentsBuilder.newInstance()
      .scheme("https")
      .host("search-api.joongna.com")
      .path("/v3/search/all")
      .encode()
      .build()
      .toUri();

  @Override
  public void start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer newProductsCount = collectProduct();
    stopWatch.stop();
    log.info("중고나라 완료: {}건, 수집 시간: {}s", newProductsCount, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("중고나라", newProductsCount, stopWatch.getTotalTimeSeconds());
    deleteOldProducts(SourceType.JUNGGO);
  }

  @Override
  public Integer collectProduct() {
    AtomicInteger updateCount = new AtomicInteger(0);
    Integer totalPage = getTotalPage();
    log.info("junggonara total page: {}", totalPage);

    IntStream.rangeClosed(0, totalPage)
        .forEach(page -> {
          JunggonaraApiRequest request = JunggonaraApiRequest.builder()
              .page(page)
              .quantity(QUANTITY)
              .sort(SORT)
              .searchWord(KEYWORD)
              .build();

          ResponseEntity<JunggonaraApiResponse> response = callApi(request);
          if (ObjectUtils.isEmpty(response.getBody())) {
            log.info("junggonara api response is empty");
          }

          List<Product> products = convertProducts(response.getBody().getData().getItems());
          updateCount.addAndGet(saveProducts(repository, products));
        });

    return updateCount.get();
  }

  public List<Product> convertProducts(List<JunggonaraItem> items) {
    return items.stream()
        .map(Product::createJunggo)
        .toList();
  }

  @Override
  public void deleteOldProducts(SourceType sourceType) {
    eventPublisher.publishEvent(sourceType);
  }

  private Integer getTotalPage() {
    JunggonaraApiRequest request = JunggonaraApiRequest.builder()
        .page(0)
        .quantity(1)
        .sort(SORT)
        .searchWord(KEYWORD)
        .build();

    ResponseEntity<JunggonaraApiResponse> response = callApi(request);
    if (ObjectUtils.isEmpty(response.getBody())) {
      log.info("junggonara api response is empty");
    }
    return division(response.getBody().getData().getTotalSize());
  }

  /**
   * 최대 10000개까지만 조회 가능한 것으로 추정(100개씩이면 0부터 99페이지까지만 요청 가능) 따라서 100페이지가 넘는다면 최대 횟수인 100을 리턴
   */
  private Integer division(Integer totalSize) {
    int totalPage = (totalSize / QUANTITY) + 1;
    return Math.min(totalPage, 100);
  }

  private ResponseEntity<JunggonaraApiResponse> callApi(JunggonaraApiRequest request) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.set("User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");
    HttpEntity<JunggonaraApiRequest> entity = new HttpEntity<>(request, headers);
    return restTemplate.exchange(uri, HttpMethod.POST, entity, JunggonaraApiResponse.class);
  }
}
