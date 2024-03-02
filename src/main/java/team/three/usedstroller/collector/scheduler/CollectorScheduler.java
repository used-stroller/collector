package team.three.usedstroller.collector.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.BunJangService;
import team.three.usedstroller.collector.service.CarrotService;
import team.three.usedstroller.collector.service.HelloMarketService;
import team.three.usedstroller.collector.service.JunggonaraService;
import team.three.usedstroller.collector.service.NaverService;

/**
 * 매일 새벽 4시마다 정해진 가견으로 실행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorScheduler {

  private final BunJangService bunJangService;
  private final HelloMarketService helloMarketService;
  private final JunggonaraService junggonaraService;
  private final NaverService naverService;
  private final CarrotService carrotService;
  private final ProductRepository productRepository;

  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void bunjang() {
    log.info("bunjang scheduler start");
    StopWatch stopWatch = new StopWatch();
    bunJangService.collectingBunJang()
        .doOnSubscribe(subscription -> stopWatch.start())
        .doOnSuccess(count -> {
          stopWatch.stop();
          log.info("번개장터 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
        })
        .subscribe();
  }

  @Scheduled(cron = "0 3 4 * * *", zone = "Asia/Seoul")
  public void hellomarket() throws JSONException, InterruptedException {
    log.info("hellomarket scheduler start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    int count = helloMarketService.collectingHelloMarket();
    stopWatch.stop();
    log.info("hellomarket running time: {} s", stopWatch.getTotalTimeSeconds());
    log.info("hellomarket complete : [{}]", count);
  }

  @Scheduled(cron = "0 10 4 * * *", zone = "Asia/Seoul")
  public void carrot() {
    log.info("carrot scheduler start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    String result = carrotService.collectingCarrotMarket(1, 100);
    stopWatch.stop();
    log.info("carrot running time: {} s", stopWatch.getTotalTimeSeconds());
    log.info(result);
  }


  @Scheduled(cron = "0 30 4 * * *", zone = "Asia/Seoul")
  public void junggonara() {
    log.info("junggonara scheduler start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    int count = junggonaraService.collectingJunggonara(1, 300);
    stopWatch.stop();
    log.info("junggonara running time: {} s", stopWatch.getTotalTimeSeconds());
    log.info("junggonara complete : [{}]", count);
  }

  @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
  public void naver() {
    log.info("naver scheduler start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    String result = naverService.collectingNaverShopping(1, 300);
    stopWatch.stop();
    log.info("naver running time: {} s", stopWatch.getTotalTimeSeconds());
    log.info(result);
  }

  @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
  public void clearOldData() {
    log.info("clear start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    productRepository.deleteAllByUpdatedAtIsBefore(LocalDateTime.now().minusDays(1));
    stopWatch.stop();
    log.info("clear running time: {} s", stopWatch.getTotalTimeSeconds());
  }

}
