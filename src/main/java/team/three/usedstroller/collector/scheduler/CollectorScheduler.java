package team.three.usedstroller.collector.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.BunJangService;
import team.three.usedstroller.collector.service.CarrotService;
import team.three.usedstroller.collector.service.JunggonaraService;
import team.three.usedstroller.collector.service.NaverService;
import team.three.usedstroller.collector.service.SecondWearService;

/**
 * 매일 새벽 4시마다 정해진 가견으로 실행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorScheduler {

  private final BunJangService bunJangService;
  private final SecondWearService secondWearService;
  private final JunggonaraService junggonaraService;
  private final NaverService naverService;
  private final CarrotService carrotService;
  private final ProductRepository productRepository;

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void bunjang() {
    log.info("번개장터 예약 수집 시작");
    StopWatch stopWatch = new StopWatch();
    bunJangService.collectingBunJang()
        .doOnSubscribe(subscription -> stopWatch.start())
        .doOnSuccess(count -> {
          stopWatch.stop();
          log.info("번개장터 예약 수집 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
        })
        .subscribe();
  }

  @Scheduled(cron = "0 5 3 * * *", zone = "Asia/Seoul")
  public void secondwear() {
    log.info("세컨웨어(구 헬로마켓) 예약 수집 시작");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer count = secondWearService.collecting();
    stopWatch.stop();
    log.info("세컨웨어 예약 수집 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
  }

  @Scheduled(cron = "0 10 3 * * *", zone = "Asia/Seoul")
  public void carrot() {
    log.info("당근 예약 수집 시작");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer count = carrotService.collectingCarrotMarket(1, 500);
    stopWatch.stop();
    log.info("댱근 예약 수집 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
  }


  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void junggonara() {
    log.info("중고나라 예약 수집 시작");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    int count = junggonaraService.collectingJunggonara(1, 300);
    stopWatch.stop();
    log.info("중고나라 예약 수집 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
  }

  @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
  public void naver() {
    log.info("네이버쇼핑 예약 수집 시작");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    String count = naverService.collectingNaverShopping(1, 300);
    stopWatch.stop();
    log.info("네이버쇼핑 예약 수집 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
  }

  @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
  public void clearOldData() {
    log.info("과거 데이터 삭제 시작");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    productRepository.deleteAllByUpdatedAtIsBefore(LocalDateTime.now().minusDays(1));
    stopWatch.stop();
    log.info("과거 데이터 삭제 완료: {}초", stopWatch.getTotalTimeSeconds());
  }

}
