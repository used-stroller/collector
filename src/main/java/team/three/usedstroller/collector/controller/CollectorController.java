package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.service.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
public class CollectorController {

	private final NaverService naverService;
	private final CarrotService carrotService;
	private final HelloMarketService helloMarketService;
	private final BunJangService bunJangService;
	private final JunggonaraService junggonaraService;

	/**
	 * 번개장터 '유모차' 검색 결과를 수집한다.
	 */
	@PostMapping("/bunjang")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int bunjang() {
		int count = bunJangService.collectingBunJang();
		log.info("bunjang test complete : [{}]", count);
		return count;
	}

	/**
	 * 중고나라 '유모차' 검색 결과를 수집한다. (약 18,000건)
	 * (양이 너무 많고 오래 걸려서 수집 갯수를 제한해야 할 듯)
	 * @param startPage 시작 페이지
	 * @param endPage 끝 페이지
	 */
	@PostMapping("/junggonara")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int junggonara(@RequestParam(required = true) Integer startPage,
	                      @RequestParam(required = true) Integer endPage) {
		int count = junggonaraService.collectingJunggonara(startPage, endPage);
		log.info("junggonara complete : [{}]", count);
		return count;
	}

	/**
	 * 헬로마켓 '유모차' 검색 결과를 수집한다.
	 */
	@PostMapping("/hello-market")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int hello() {
		int count = helloMarketService.collectingHelloMarket();
		log.info("hello market complete : [{}]", count);
		return count;
	}

	/**
	 * 당근마켓 '유모차' 검색 결과를 수집한다.
	 * 더보기 검색 수집은 100 페이지까지만 가능하다. 101 페이지부터 빈 페이지를 응답받는다.
	 * "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market?page="
	 * @param startPage 시작 페이지
	 */
	@PostMapping("/carrot-market")
	@ResponseStatus(HttpStatus.CREATED)
	public void carrotMarket(@RequestParam(required = true) Integer startPage) {

		log.info("carrot market collector start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String url = UriComponentsBuilder.newInstance()
				.scheme("https")
				.host("www.daangn.com")
				.path("/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market")
				.queryParam("page", "")
				.build().toUriString();
		String result = carrotService.collectingCarrotMarket(url, startPage);
		stopWatch.stop();
		log.info(result);
		log.info("total running time: {} s", stopWatch.getTotalTimeSeconds());
	}

	/**
	 * 네이버쇼핑 '유모차' 메이저 브랜드로 필터링 된 검색 결과를 수집한다.
	 * "https://search.shopping.naver.com/search/all" +
	 * "?brand=27112%20215978%2029436%20215480%2026213%20219842%2028497%2013770%20236955%20151538%20242564%2028546" +
	 * "&frm=NVSHBRD&origQuery=%EC%9C%A0%EB%AA%A8%EC%B0%A8" +
	 * "&pagingSize=40&productSet=total&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8&sort=rel&timestamp=&viewType=list" +
	 * "&pagingIndex=";
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
		String url = UriComponentsBuilder.newInstance()
				.scheme("https")
				.host("search.shopping.naver.com")
				.path("/search/all")
				.queryParam("brand", "27112%20215978%2029436%20215480%2026213%20219842%2028497%2013770%20236955%20151538%20242564%2028546")
				.queryParam("frm", "NVSHBRD")
				.queryParam("origQuery", "%EC%9C%A0%EB%AA%A8%EC%B0%A8")
				.queryParam("pagingSize", "40")
				.queryParam("productSet", "total")
				.queryParam("query", "%EC%9C%A0%EB%AA%A8%EC%B0%A8")
				.queryParam("sort", "rel")
				.queryParam("timestamp", "")
				.queryParam("viewType", "list")
				.queryParam("pagingIndex", "")
				.build().toUriString();
		String result = naverService.collectingNaverShopping(url, startPage, endPage);
		stopWatch.stop();
		log.info(result);
		log.info("total running time: {} s", stopWatch.getTotalTimeSeconds());
	}

}
