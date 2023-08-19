package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.three.usedstroller.collector.domain.ProductDto;
import team.three.usedstroller.collector.service.CollectorService;

import javax.annotation.PostConstruct;
import javax.script.ScriptException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
public class CollectorController {

	private final CollectorService collectorService;

	@GetMapping("/naver-shopping")
	@ResponseStatus(HttpStatus.OK)
	public void naverShopping() {
		log.info("naver shopping");
		String url = "https://search.shopping.naver.com/search/all?where=all&frm=NVSCTAB&query=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
		collectorService.collectingNaverShopping(url);
	}

	@GetMapping("/naver-test")
	@ResponseStatus(HttpStatus.OK)
	public void test() {
		log.info("naver test");
		String url = "https://finance.naver.com/world/";
		collectorService.collectingNaver(url);
	}
	@GetMapping("/bunjang-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int bunjang() throws InterruptedException {
		log.info("bunjang test");
		int complete = collectorService.collectingBunJang();
		return complete;
	}

	@GetMapping("/junggo-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int junggo() throws InterruptedException {
		int completeCount = collectorService.collectingJunggonara();
		return completeCount;
	}

	@GetMapping("/hello-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int hello() throws InterruptedException, ScriptException {
		int completeCnt = collectorService.collectingHelloMarket();
		return completeCnt;
	}

}
