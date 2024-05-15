package team.three.usedstroller.collector.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.three.usedstroller.collector.service.mvc.BunJangServiceMvc;
import team.three.usedstroller.collector.service.mvc.CarrotServiceMvc;
import team.three.usedstroller.collector.service.mvc.CommonService;
import team.three.usedstroller.collector.service.mvc.JunggonaraServiceMvc;
import team.three.usedstroller.collector.service.mvc.NaverServiceMvc;
import team.three.usedstroller.collector.service.mvc.SecondWearServiceMvc;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
@Tag(name = "collector", description = "수집기")
public class CollectorController {

  private final NaverServiceMvc naverService;
  private final CarrotServiceMvc carrotService;
  private final BunJangServiceMvc bunJangService;
  private final JunggonaraServiceMvc junggonaraService;
  private final SecondWearServiceMvc secondWearService;
  private final CommonService commonService;

  /**
   * 번개장터 '유모차' 검색 결과를 수집한다.
   *
   * @runningTime 5초 (약 2800건)
   */
  @PostMapping("/bunjang")
  @ResponseStatus(HttpStatus.CREATED)
  public void bunjang() {
    bunJangService.start();
  }

  /**
   * 중고나라 '유모차' 검색 결과를 수집한다. (약 18,000건)
   *
   * @runningTime 약 20초 (약 10,000건)
   */
  @PostMapping("/junggonara")
  @ResponseStatus(HttpStatus.CREATED)
  public void junggonara() {
    junggonaraService.start();
  }

  /**
   * 세컨웨어(구 헬로마켓) '유모차' 검색 결과를 수집한다.
   *
   * @runningTime 약 10초 (약 600건)
   */
  @PostMapping("/secondwear")
  @ResponseStatus(HttpStatus.CREATED)
  public void secondwear() {
    secondWearService.start();
  }

  /**
   * 당근마켓 '유모차' 검색 결과를 수집한다.
   * "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market?page="
   *
   * @runningTime 500페이지에 약 30분 (약 3000건)
   */
  @PostMapping("/carrot-market")
  @ResponseStatus(HttpStatus.CREATED)
  public void carrotMarket() {
    carrotService.start();
  }

  /**
   * 네이버쇼핑 검색 API를 이용해서 '유모차' 검색 결과를 수집한다.
   *
   * @runningTime 약 10초 (1000건)
   */
  @PostMapping("/naver-shopping")
  @ResponseStatus(HttpStatus.CREATED)
  public void naverShopping() {
    naverService.start();
  }

  @PostMapping("/collect/all")
  @ResponseStatus(HttpStatus.CREATED)
  public void collectAll() {
    commonService.collectAll();
  }

  @PostMapping("/update/model")

  @ResponseStatus(HttpStatus.CREATED)
  public void updateModel() {
    commonService.updateModel();
  }
}
