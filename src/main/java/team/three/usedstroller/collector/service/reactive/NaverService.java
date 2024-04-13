package team.three.usedstroller.collector.service.reactive;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.dto.NaverApiResponse;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.util.ApiService;
import team.three.usedstroller.collector.util.SlackHook;

@Slf4j
@Service
public class NaverService extends CommonService {

  private final ApiService apiService;
  private final SlackHook slackHook;
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
  ParameterizedTypeReference<NaverApiResponse> typeReference = new ParameterizedTypeReference<>() {
  };
  //  @Value("${naver.id}")
  private String id;
  //  @Value("${naver.secret}")
  private String secret;


  public NaverService(ProductRepository productRepository,
      ApplicationEventPublisher eventPublisher, ApiService apiService, SlackHook slackHook) {
    super(productRepository, eventPublisher);
    this.apiService = apiService;
    this.slackHook = slackHook;
  }

  public void start() {
    StopWatch stopWatch = new StopWatch();
    collecting()
        .doOnSubscribe(subscription -> stopWatch.start())
        .doOnSuccess(count -> {
          stopWatch.stop();
          log.info("네이버쇼핑 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
          slackHook.sendMessage("네이버쇼핑", count, stopWatch.getTotalTimeSeconds());
        })
        .onErrorStop()
        .publishOn(Schedulers.boundedElastic())
        .doFinally(f -> super.deleteOldData(SourceType.NAVER))
        .subscribe();
  }

  /**
   * 요청 가능한 최대 page 수 100개씩 1000페이지(10만건) 총 상품 수가 45만건이 넘기 때문에 한도까지 모두 수집
   */
  public Mono<Integer> collecting() {
    AtomicInteger updateCount = new AtomicInteger(0);
    Consumer<HttpHeaders> headerConsumer = headers -> {
      headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
      headers.add("X-Naver-Client-Id", id);
      headers.add("X-Naver-Client-Secret", secret);
    };

    return Flux.range(0, 10)
        .map(start -> start * 100 + 1)
        .delayElements(Duration.ofMillis(500))
        .flatMap(start -> {
          String url = uriBuilder
              .replaceQueryParam("start", start * 100 + 1)
              .build()
              .toUriString();
          return apiService.apiCallGet(url, typeReference, headerConsumer,
                  MediaType.APPLICATION_JSON, true)
              .switchIfEmpty(Mono.defer(Mono::empty))
              .onErrorResume(e -> Mono.error(new RuntimeException("naver api connect error", e)))
              .publishOn(Schedulers.boundedElastic())
              .flatMap(res -> saveItemList(res.getItems())
                  .flatMap(count -> {
                    log.info("naver start: [{}], saved item: [{}], total update: [{}]", start,
                        count, updateCount.addAndGet(count));
                    return Mono.just(count);
                  }));
        })
        .reduce(Integer::sum);
  }

  public Mono<Integer> saveItemList(List<NaverApiResponse.Items> list) {
    return Flux.fromIterable(list)
        .flatMap(item -> Mono.just(Product.createNaver(item)))
        .collectList()
        .flatMap(this::saveProducts);
  }

}
