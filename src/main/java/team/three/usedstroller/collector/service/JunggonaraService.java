package team.three.usedstroller.collector.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.dto.JunggonaraApiRequest;
import team.three.usedstroller.collector.domain.dto.JunggonaraApiResponse;
import team.three.usedstroller.collector.domain.dto.JunggonaraItem;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.util.ApiService;
import team.three.usedstroller.collector.util.SlackHook;

@Service
@Slf4j
public class JunggonaraService extends CommonService {

  private final ApiService apiService;
  private final SlackHook slackHook;
  private final String URL = "https://search-api.joongna.com/v3/search/all";
  private final String SORT = "RECENT_SORT";
  private final String KEYWORD = "유모차";
  private final Integer QUANTITY = 100;
  ParameterizedTypeReference<JunggonaraApiResponse> typeReference = new ParameterizedTypeReference<>() {
  };

  public JunggonaraService(ProductRepository productRepository,
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
          log.info("중고나라 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
          slackHook.sendMessage("중고나라", count, stopWatch.getTotalTimeSeconds());
        })
        .publishOn(Schedulers.boundedElastic())
        .doFinally(f -> super.deleteOldData(SourceType.JUNGGO))
        .subscribe();
  }


  public Mono<Integer> collecting() {
    AtomicInteger updateCount = new AtomicInteger(0);

    return getTotalPage()
        .flatMap(totalPage -> {
          log.info("junggonara total page: {}", totalPage);

          return Flux.range(0, totalPage)
              .flatMap(page -> {
                JunggonaraApiRequest request = JunggonaraApiRequest.builder()
                    .page(page)
                    .quantity(QUANTITY)
                    .sort(SORT)
                    .searchWord(KEYWORD)
                    .build();

                return apiService.apiCallPost(URL, request, typeReference,
                        MediaType.APPLICATION_JSON)
                    .switchIfEmpty(Mono.defer(Mono::empty))
                    .onErrorResume(
                        e -> Mono.error(new RuntimeException("junggonara api connect error", e)))
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(res -> saveItemList(res.getData().getItems())
                        .flatMap(count -> {
                          log.info("junggonara page: [{}], saved item: [{}], total update: [{}]",
                              page, count, updateCount.addAndGet(count));
                          return Mono.just(count);
                        }));
              })
              .reduce(Integer::sum);
        });
  }

  public Mono<Integer> saveItemList(List<JunggonaraItem> list) {
    return Flux.fromIterable(list)
        .flatMap(Product::createJunggo)
        .collectList()
        .flatMap(this::saveProducts);
  }

  private Mono<Integer> getTotalPage() {
    JunggonaraApiRequest request = JunggonaraApiRequest.builder()
        .page(0)
        .quantity(1)
        .sort(SORT)
        .searchWord(KEYWORD)
        .build();
    return apiService.apiCallPost(URL, request, typeReference, MediaType.APPLICATION_JSON)
        .switchIfEmpty(Mono.defer(Mono::empty))
        .onErrorResume(
            e -> Mono.error(new IllegalArgumentException("junggonara api connect error", e)))
        .flatMap(res -> Mono.just(division(res.getData().getTotalSize())));
  }

  /**
   * 최대 10000개까지만 조회 가능한 것으로 추정(100개씩이면 0부터 99페이지까지만 요청 가능) 따라서 100페이지가 넘는다면 최대 횟수인 100을 리턴
   */
  private Integer division(Integer totalSize) {
    int totalPage = (totalSize / QUANTITY) + 1;
    return Math.min(totalPage, 100);
  }

}
