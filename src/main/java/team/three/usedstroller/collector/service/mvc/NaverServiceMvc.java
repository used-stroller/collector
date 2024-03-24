package team.three.usedstroller.collector.service.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
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

  @Value("${naver.id}")
  private String id;
  @Value("${naver.secret}")
  private String secret;
  @Value("${naver.url}")
  private String url;

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
          String urlWithPage = url + (page * 100 + 1);
          Map<String, String> requestHeaders = new HashMap<>();
          requestHeaders.put("X-Naver-Client-Id", id);
          requestHeaders.put("X-Naver-Client-Secret", secret);
          HttpHeaders headers = new HttpHeaders();
          headers.setAll(requestHeaders);

          ResponseEntity<NaverApiResponse> response = restTemplate.exchange(urlWithPage,
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
