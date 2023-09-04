package team.three.usedstroller.collector.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import team.three.usedstroller.collector.service.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorScheduler {

	private final BunJangService bunJangService;
	private final HelloMarketService helloMarketService;
	private final JunggonaraService junggonaraService;
	private final NaverService naverService;
	private final CarrotService carrotService;

	/**
	 * 매일 6시간마다 실행
	 */
	@Scheduled(cron = "0 0 */6 * * *", zone = "Asia/Seoul")
	public void hellomarket() {
		log.info("hellomarket scheduler start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int count = helloMarketService.collectingHelloMarket();
		stopWatch.stop();
		log.info("hellomarket running time: {} s", stopWatch.getTotalTimeSeconds());
		log.info("hellomarket complete : [{}]", count);
	}

	@Scheduled(cron = "0 3 */6 * * *", zone = "Asia/Seoul")
	public void carrot() {
		log.info("carrot scheduler start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String result = carrotService.collectingCarrotMarket(1);
		stopWatch.stop();
		log.info("carrot running time: {} s", stopWatch.getTotalTimeSeconds());
		log.info(result);
	}

	/**
	 * 매일 오전 6시, 오후 6시 6분에 실행한다.
	 */
	@Scheduled(cron = "0 6 */6 * * *", zone = "Asia/Seoul")
	public void bunjang() {
		log.info("bunjang scheduler start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int count = bunJangService.collectingBunJang();
		stopWatch.stop();
		log.info("bunjang running time: {} s", stopWatch.getTotalTimeSeconds());
		log.info("bunjang complete : [{}]", count);
	}

	/**
	 * 매일 오전 6시, 오후 6시 12분에 실행한다.
	 */
	@Scheduled(cron = "0 12 */6 * * *", zone = "Asia/Seoul")
	public void junggonara() {
		log.info("junggonara scheduler start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int count = junggonaraService.collectingJunggonara(1, 300);
		stopWatch.stop();
		log.info("junggonara running time: {} s", stopWatch.getTotalTimeSeconds());
		log.info("junggonara complete : [{}]", count);
	}

	/**
	 * 매일 오전 6시, 오후 6시 22분에 실행한다.
	 */
	@Scheduled(cron = "0 22 */6 * * *", zone = "Asia/Seoul")
	public void naver() {
		log.info("naver scheduler start");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String result = naverService.collectingNaverShopping(1, 300);
		stopWatch.stop();
		log.info("naver running time: {} s", stopWatch.getTotalTimeSeconds());
		log.info(result);
	}

}
