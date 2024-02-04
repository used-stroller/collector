package team.three.usedstroller.collector.service;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static team.three.usedstroller.collector.validation.PidDuplicationValidator.isNotExistPid;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.service.dto.BunjangApiResponse;
import team.three.usedstroller.collector.service.dto.BunjangItem;
import team.three.usedstroller.collector.util.ApiService;

@Service
@Slf4j
@RequiredArgsConstructor
public class BunJangService {

	private final ProductRepository productRepository;
	private final ApiService apiService;
	ParameterizedTypeReference<BunjangApiResponse> typeReference = new ParameterizedTypeReference<>() {};

	/**
	 * api url: https://api.bunjang.co.kr/api/1/find_v2.json?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8&page=0&n=200
	 * page: 0 (페이지 번호)
	 * n: 200 (한번에 가져오는 상품 수)
	 */
	public Mono<Integer> collectingBunJang() {
		return getTotalPageBunJang()
			.flatMap(totalCount -> {
				int totalPage = totalCount / 200;
				log.info("bunjang total page: {}", totalPage);

				return Flux.range(0, totalPage)
//						.delayElements(Duration.ofMillis(1000)) // 1초 간격으로 호출
						.flatMap(page -> {
							String url = "https://api.bunjang.co.kr/api/1/find_v2.json?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8&n=200&page=" + page;

							return apiService.apiCallGet(url, typeReference, MediaType.APPLICATION_JSON, true)
								.switchIfEmpty(Mono.defer(Mono::empty))
								.onErrorResume(e -> Mono.error(new RuntimeException("bunjang api connect error", e)))
								.publishOn(Schedulers.boundedElastic())
								.flatMap(res -> {
									return saveItemList(res.getList())
										.flatMap(count -> {
											log.info("bunjang page: [{}], saved item: [{}]", page, count);
											return Mono.just(count);
										})
										.onErrorResume(e -> Mono.error(new RuntimeException("bunjang save error", e)));
								});
						})
						.reduce(0, Integer::sum);
			});
	}

	public Mono<Integer> saveItemList(List<BunjangItem> list) {
		List<Product> items = new ArrayList<>();

		for (BunjangItem item : list) {
			Product product = Product.createBunJang(item);
			if (isNotExistPid(productRepository, product)) {
				items.add(product);
			}
		}

		productRepository.saveAll(items);
		return Mono.just(items.size());
	}

	private Mono<Integer> getTotalPageBunJang() {
		String url = "https://api.bunjang.co.kr/api/1/find_v2.json?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8&page=0&n=0";
		return apiService.apiCallGet(url, typeReference, MediaType.APPLICATION_JSON, true)
				.switchIfEmpty(Mono.empty())
				.flatMap(res -> Mono.just(res.getNumFound()))
				.onErrorResume(e -> Mono.error(new RuntimeException("bunjang totalCount api connect error", e)));
	}

}
