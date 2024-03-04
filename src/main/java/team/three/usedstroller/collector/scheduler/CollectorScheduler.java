package team.three.usedstroller.collector.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void bunjang() {
    log.info("번개장터 예약 수집 시작");
    bunJangService.start();
  }

  @Scheduled(cron = "0 5 3 * * *", zone = "Asia/Seoul")
  public void secondwear() {
    log.info("세컨웨어(구 헬로마켓) 예약 수집 시작");
    secondWearService.start();
  }

  @Scheduled(cron = "0 10 3 * * *", zone = "Asia/Seoul")
  public void junggonara() {
    log.info("중고나라 예약 수집 시작");
    junggonaraService.start();
  }

  @Scheduled(cron = "0 15 3 * * *", zone = "Asia/Seoul")
  public void carrot() {
    log.info("당근 예약 수집 시작");
    carrotService.start(1, 500);
  }

  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void naver() {
    log.info("네이버쇼핑 예약 수집 시작");
    naverService.start(1, 200);
  }

}
