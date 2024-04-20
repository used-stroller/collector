package team.three.usedstroller.collector.service.mvc;

import static team.three.usedstroller.collector.util.DefaultHttpHeaders.getDefaultHeaders;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.dto.SecondWearApiResponse;
import team.three.usedstroller.collector.domain.dto.SecondWearItem;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.ProductCollector;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecondWearServiceMvc implements ProductCollector {

  private final ProductRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final RestTemplate restTemplate;
  private final SlackHook slackHook;
  private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
      .scheme("https")
      .host("hellomarket.com")
      .path("api/search/items")
      .queryParam("q", "유모차")
      .queryParam("page", 1)
      .queryParam("limit", 20)
      .encode();

  @Override
  public void start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer newProductsCount = collectProduct();
    stopWatch.stop();
    log.info("세컨웨어 완료: {}건, 수집 시간: {}s", newProductsCount, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("세컨웨어", newProductsCount, stopWatch.getTotalTimeSeconds());
    deleteOldProducts(SourceType.SECOND);
  }

  /**
   * api url: https://hellomarket.com/api/search/items?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8&page=1&limit=20
   * (페이지 번호) limit: 20 (한번에 가져오는 상품 수)
   */
  @Override
  public Integer collectProduct() {
    AtomicInteger updateCount = new AtomicInteger(0);
    Integer totalCount = getTotalCount();
    int totalPage = totalCount / 20;
    log.info("second total page: {}", totalPage);

    IntStream.rangeClosed(1, totalPage) // 0 부터 totalPage 까지 반복
        .forEach(page -> {
          URI uri = uriBuilder
              .replaceQueryParam("page", page)
              .build()
              .toUri();

          ResponseEntity<SecondWearApiResponse> response = restTemplate.exchange(uri,
              HttpMethod.GET,
              new HttpEntity<>(getDefaultHeaders()), SecondWearApiResponse.class);

          if (ObjectUtils.isEmpty(response.getBody())) {
            log.info("secondWear api response is null. page: {}", page);
            return;
          }
          List<Product> products = convertProducts(response.getBody().getList());
          updateCount.addAndGet(saveProducts(repository, products));
        });

    return updateCount.get();
  }

  public List<Product> convertProducts(List<SecondWearItem> items) {
    return items.stream()
        .map(Product::createSecondwear)
        .toList();
  }

  @Override
  public void deleteOldProducts(SourceType sourceType) {
    eventPublisher.publishEvent(sourceType);
  }

  private Integer getTotalCount() {
    URI uri = uriBuilder
        .build()
        .toUri();

    ResponseEntity<SecondWearApiResponse> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(getDefaultHeaders()),
        SecondWearApiResponse.class);

    return Objects.requireNonNull(response.getBody()).getResult().getTotalCount();
  }

}
