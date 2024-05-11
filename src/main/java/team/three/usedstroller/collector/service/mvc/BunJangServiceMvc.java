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
import team.three.usedstroller.collector.domain.Keyword;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.dto.BunjangApiResponse;
import team.three.usedstroller.collector.domain.dto.BunjangItem;
import team.three.usedstroller.collector.repository.KeywordRepository;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.ProductCollector;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
@RequiredArgsConstructor
public class BunJangServiceMvc implements ProductCollector {

  private final KeywordRepository keywordRepository;
  private final ProductRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final RestTemplate restTemplate;
  private final SlackHook slackHook;
  private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
      .scheme("https")
      .host("api.bunjang.co.kr")
      .path("api/1/find_v2.json")
      .queryParam("n", 200)
      .queryParam("page", 0)
      .encode();

  @Override
  public Integer start() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer newProductsCount = collectProduct();
    stopWatch.stop();
    log.info("번개장터 완료: {}건, 수집 시간: {}s", newProductsCount, stopWatch.getTotalTimeSeconds());
    slackHook.sendMessage("번개장터", newProductsCount, stopWatch.getTotalTimeSeconds());
    deleteOldProducts(SourceType.BUNJANG);
    return newProductsCount;
  }

  /**
   * api url:
   * https://api.bunjang.co.kr/api/1/find_v2.json?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8&page=0&n=200 page: 0
   * (페이지 번호) n: 200 (한번에 가져오는 상품 수)
   */
  @Override
  public Integer collectProduct() {
    AtomicInteger updateCount = new AtomicInteger(0);
    List<Keyword> keywordList = keywordRepository.findAll();
    for (Keyword keyword : keywordList) {
      log.info("keyword : {}", keyword.getKeyword());
      uriBuilder.queryParam("q", keyword.getKeyword());
      Integer totalCount = getTotalCount();
      int totalPage = totalCount / 200;
      log.info("bunjang total page: {}", totalPage);

      IntStream.rangeClosed(0, totalPage) // 0 부터 totalPage 까지 반복
          .forEach(page -> {
            URI uri = uriBuilder
                .replaceQueryParam("page", page)
                .build()
                .toUri();

            ResponseEntity<BunjangApiResponse> response = restTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(getDefaultHeaders()), BunjangApiResponse.class);

            if (ObjectUtils.isEmpty(response.getBody())) {
              log.info("bunjang api response is null. page: {}", page);
              return;
            }
            List<Product> products = convertProducts(response.getBody().getList());
            updateCount.addAndGet(saveProducts(repository, products));
          });
    }
    return updateCount.get();
  }

  public List<Product> convertProducts(List<BunjangItem> items) {
    return items.stream()
        .map(Product::createBunJang)
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

    ResponseEntity<BunjangApiResponse> response = restTemplate.exchange(uri, HttpMethod.GET,
        new HttpEntity<>(getDefaultHeaders()),
        BunjangApiResponse.class);

    return Objects.requireNonNull(response.getBody()).getNumFound();
  }

}
