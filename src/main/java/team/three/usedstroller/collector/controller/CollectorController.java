package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.three.usedstroller.collector.service.CollectorService;
import team.three.usedstroller.collector.service.SecondhandService;

import javax.annotation.PostConstruct;
import javax.script.ScriptException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
public class CollectorController {

	private final CollectorService collectorService;
	private final SecondhandService secondhandService;

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
		int complete = secondhandService.collectingBunJang();
		return complete;
	}

	@GetMapping("/junggo-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int junggo() throws InterruptedException {
		int completeCount = secondhandService.collectingJunggonara();
		return completeCount;
	}

	@PostConstruct
	@GetMapping("/hello-test")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public int hello() throws InterruptedException, ScriptException {
		int completeCnt = secondhandService.collectingHelloMarket();
		return completeCnt;
	}

}
