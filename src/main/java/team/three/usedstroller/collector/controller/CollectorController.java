package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.three.usedstroller.collector.service.CollectorService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/collector")
public class CollectorController {

	private final CollectorService collectorService;

	@GetMapping("/naver-test")
	@ResponseStatus(HttpStatus.OK)
	public void test() {
		log.info("naver test");
		String url = "https://finance.naver.com/world/";
		collectorService.collectingNaver(url);
	}

}
