package team.three.usedstroller.collector.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.three.usedstroller.collector.service.BunJangService;
import team.three.usedstroller.collector.service.CarrotService;
import team.three.usedstroller.collector.service.JunggonaraService;
import team.three.usedstroller.collector.service.NaverService;
import team.three.usedstroller.collector.service.SecondWearService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
@Tag(name = "collector", description = "수집기")
public class CollectorController {

	private final NaverService naverService;
	private final CarrotService carrotService;
	private final SecondWearService secondWearService;
	private final BunJangService bunJangService;
	private final JunggonaraService junggonaraService;

	/**
	 * 번개장터 '유모차' 검색 결과를 수집한다.
	 * @runningTime 5초 (약 2800건)
	 */
	@PostMapping("/bunjang")
	@ResponseStatus(HttpStatus.OK)
	public void bunjang() {
		StopWatch stopWatch = new StopWatch();
		bunJangService.collectingBunJang()
				.doOnSubscribe(subscription -> stopWatch.start())
				.doOnSuccess(count -> {
					stopWatch.stop();
					log.info("번개장터 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
				})
				.subscribe();
	}

	/**
	 * 중고나라 '유모차' 검색 결과를 수집한다. (약 18,000건)
	 * (양이 너무 많고 오래 걸려서 수집 갯수를 제한해야 할 듯)
	 * @param startPage 시작 페이지
	 * @param endPage 끝 페이지 (약 125페이지)
	 * @runningTime 16분 (약 10,000건)
	 */
	@PostMapping("/junggonara")
	@ResponseStatus(HttpStatus.OK)
	public int junggonara(
			@RequestParam(required = true) Integer startPage,
			@RequestParam(required = true) Integer endPage) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int count = junggonaraService.collectingJunggonara(startPage, endPage);
		stopWatch.stop();
		log.info("중고나라 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
		return count;
	}

  /**
   * 세컨웨어(구 헬로마켓) '유모차' 검색 결과를 수집한다.
   * @runningTime 42초 (약 571건)
   */
  @PostMapping("/secondwear")
  @ResponseStatus(HttpStatus.OK)
  public int secondwear() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Integer count = secondWearService.collecting();
    stopWatch.stop();
		log.info("세컨웨어 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
    return count;
  }

	/**
	 * 당근마켓 '유모차' 검색 결과를 수집한다.
	 * 더보기 검색 수집은 100 페이지까지만 가능하다. 101 페이지부터 빈 페이지를 응답받는다.
	 * "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market?page="
	 * @param startPage 시작 페이지
	 * @runningTime 약 6분 (약 1000건)
	 */
	@PostMapping("/carrot-market")
	@ResponseStatus(HttpStatus.CREATED)
	public void carrotMarket(
			@RequestParam(required = true) Integer startPage,
			@RequestParam(required = true, defaultValue = "100") Integer endPage) {

		log.info("carrot market collector start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Integer count = carrotService.collectingCarrotMarket(startPage, endPage);
		stopWatch.stop();
		log.info("댱근 완료: {}건, 수집 시간: {}s", count, stopWatch.getTotalTimeSeconds());
	}

	/**
	 * 네이버쇼핑 '유모차' 메이저 브랜드로 필터링 된 검색 결과를 수집한다.
	 * "https://search.shopping.naver.com/search/all" +
	 * "?brand=27112%20215978%2029436%20215480%2026213%20219842%2028497%2013770%20236955%20151538%20242564%2028546" +
	 * "&frm=NVSHBRD&origQuery=%EC%9C%A0%EB%AA%A8%EC%B0%A8" +
	 * "&pagingSize=40&productSet=total&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8&sort=rel&timestamp=&viewType=list" +
	 * "&pagingIndex=";
	 * @param startPage 시작 페이지
	 * @param endPage 끝 페이지 (약 150페이지)
	 * @runningTime 약 70분 (약 6000건)
	 */
	@PostMapping("/naver-shopping")
	@ResponseStatus(HttpStatus.CREATED)
	public void naverShopping(
			@RequestParam(required = true) Integer startPage,
			@RequestParam(required = false, defaultValue = "200") Integer endPage) {

		log.info("naver shopping collector start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String result = naverService.collectingNaverShopping(startPage, endPage);
		stopWatch.stop();
		log.info("네이버쇼핑 완료: {}, 수집 시간: {}s", result, stopWatch.getTotalTimeSeconds());
	}

}
