package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.three.usedstroller.collector.service.CollectorService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
public class CollectorController {

	private final CollectorService collectorService;

	@PostMapping("/naver-shopping")
	@ResponseStatus(HttpStatus.CREATED)
	public void naverShopping(@RequestParam(required = true) Integer startPage,
							  @RequestParam(required = true) Integer endPage) {

		log.info("naver shopping collector start");
		String url = "https://search.shopping.naver.com/search/all" +
				"?brand=27112%20215978%2029436%20215480%2026213%20219842%2028497%2013770%20236955%20151538%20242564%2028546" +
				"&frm=NVSHBRD&origQuery=%EC%9C%A0%EB%AA%A8%EC%B0%A8" +
				"&pagingSize=40&productSet=total&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8&sort=rel&timestamp=&viewType=list" +
				"&pagingIndex=";
		String result = collectorService.collectingNaverShopping(url, startPage, endPage);
		log.info(result);
	}

}
