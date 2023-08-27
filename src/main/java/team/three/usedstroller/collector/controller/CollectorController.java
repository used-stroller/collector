package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import team.three.usedstroller.collector.service.CarrotService;
import team.three.usedstroller.collector.service.NaverService;
import team.three.usedstroller.collector.service.CollectorService;
import team.three.usedstroller.collector.service.SecondhandService;

import javax.script.ScriptException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
public class CollectorController {

	private final NaverService naverService;
	private final CarrotService carrotService;
	private final CollectorService collectorService;
	private final SecondhandService secondhandService;

	/**
	 * 번개장터 '유모차' 검색 결과를 수집한다.
	 */
	@GetMapping("/bunjang-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int bunjang() throws InterruptedException {
		log.info("bunjang test");
		int complete = secondhandService.collectingBunJang();
		return complete;
	}

	/**
	 * 중고나라 '유모차' 검색 결과를 수집한다.
	 */
	@GetMapping("/junggo-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int junggo() throws InterruptedException {
		int completeCount = secondhandService.collectingJunggonara();
		return completeCount;
	}

	/**
	 * 헬로마켓 '유모차' 검색 결과를 수집한다.
	 */
	@GetMapping("/hello-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int hello() throws InterruptedException, ScriptException {
		int completeCnt = secondhandService.collectingHelloMarket();
		return completeCnt;
	}

	/**
	 * 당근마켓 '유모차' 검색 결과를 수집한다.
	 * 더보기 검색 수집은 100 페이지까지만 가능하다. 101 페이지부터 빈 페이지를 응답받는다.
	 * @param startPage 시작 페이지
	 */
	@PostMapping("/carrot-market")
	@ResponseStatus(HttpStatus.CREATED)
	public void carrotMarket(@RequestParam(required = true) Integer startPage) {

		log.info("carrot market collector start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String pagesUrl = "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/";
		int endPage = carrotService.getTotalPages(pagesUrl);
		String url = "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market?page=";
		String result = carrotService.collectingCarrotMarket(url, startPage, endPage);
		stopWatch.stop();
		log.info(result);
		log.info("total running time: {} s", stopWatch.getTotalTimeSeconds());
	}

	/**
	 * 네이버쇼핑 '유모차' 메이저 브랜드로 필터링 된 검색 결과를 수집한다.
	 * @param startPage 시작 페이지
	 * @param endPage 끝 페이지
	 */
	@PostMapping("/naver-shopping")
	@ResponseStatus(HttpStatus.CREATED)
	public void naverShopping(@RequestParam(required = true) Integer startPage,
													  @RequestParam(required = true) Integer endPage) {

		log.info("naver shopping collector start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String url = "https://search.shopping.naver.com/search/all" +
				"?brand=27112%20215978%2029436%20215480%2026213%20219842%2028497%2013770%20236955%20151538%20242564%2028546" +
				"&frm=NVSHBRD&origQuery=%EC%9C%A0%EB%AA%A8%EC%B0%A8" +
				"&pagingSize=40&productSet=total&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8&sort=rel&timestamp=&viewType=list" +
				"&pagingIndex=";
		String result = naverService.collectingNaverShopping(url, startPage, endPage);
		stopWatch.stop();
		log.info(result);
		log.info("total running time: {} s", stopWatch.getTotalTimeSeconds());
	}

}
