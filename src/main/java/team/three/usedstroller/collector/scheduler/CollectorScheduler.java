package team.three.usedstroller.collector.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team.three.usedstroller.collector.service.mvc.BunJangServiceMvc;
import team.three.usedstroller.collector.service.mvc.CarrotServiceMvc;
import team.three.usedstroller.collector.service.mvc.CommonService;
import team.three.usedstroller.collector.service.mvc.JunggonaraServiceMvc;
import team.three.usedstroller.collector.service.mvc.NaverServiceMvc;
import team.three.usedstroller.collector.service.mvc.SecondWearServiceMvc;

/**
 * 매일 새벽 4시마다 정해진 가견으로 실행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorScheduler {

  private final BunJangServiceMvc bunJangService;
  private final JunggonaraServiceMvc junggonaraService;
  private final NaverServiceMvc naverService;
  private final CarrotServiceMvc carrotService;
  private final SecondWearServiceMvc secondWearService;
  private final CommonService commonService;

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
  public void naver() {
    log.info("네이버쇼핑 예약 수집 시작");
    naverService.start();
  }

  @Scheduled(cron = "0 20 3 * * *", zone = "Asia/Seoul")
  public void carrot() {
    log.info("당근 예약 수집 시작");
    carrotService.start();
    commonService.updateModel();
  }

  @Scheduled(cron = "0 30 15 * * *", zone = "Asia/Seoul")
  public void collectAll() {
    log.info("collectAll start");
    carrotService.start();
    commonService.collectAll();
  }

//  @Scheduled(cron = "0 00 6 * * *", zone = "Asia/Seoul")
//  public void nullDate() {
//    log.info("업로드 데이트 업데이트 ");
//    commonService.updateNullDate();
//  }


}
