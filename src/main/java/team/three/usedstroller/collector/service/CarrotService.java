package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponentsBuilder;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarrotService {

	private final ProductRepository productRepository;
	private final String url = UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("www.daangn.com")
			.path("/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market")
			.queryParam("page", "")
			.build().toUriString();

	public String collectingCarrotMarket(Integer startPage) {
		int endPage = getTotalPages();
		log.info("carrot market total page: {}", endPage);

		for (int page = startPage; page <= endPage; page++) {
			try {
				List<Product> carrots = crawlingCarrotPage(url + page);
				if (ObjectUtils.isEmpty(carrots)) {
					log.info("carrot market page: [{}] is empty", page);
					endPage = page - 1;
					break;
				}
				List<Product> result = saveCarrots(carrots);
				log.info("carrot market page: [{}], saved item: [{}]", page, result.size());
			} catch (Exception e) {
				throw new RuntimeException("carrot market connect error", e);
			}
		}
		return "carrot market collector complete. total page: " + endPage;
	}

	@Transactional
	public List<Product> saveCarrots(List<Product> items) {
		return productRepository.saveAll(items);
	}

	private List<Product> crawlingCarrotPage(String url) throws IOException {
		List<Product> list = new ArrayList<>();
		Document doc = Jsoup.connect(url).get();
		doc.select("article.flea-market-article")
				.forEach(element -> {
					String title = element.select("span.article-title").text();
					String content = element.select("span.article-content").text();
					String region = element.select("p.article-region-name").text();
					String price = element.select("p.article-price").text();
					String imgSrc = element.select("div.card-photo > img").attr("src");
					String link = element.select("a.flea-market-article-link").attr("href");
					Product product = Product.createCarrot(title, price, region, link, imgSrc, content);
					list.add(product);
				});
		return list;
	}

	private int getTotalPages() {
		String url = "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/";
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new RuntimeException("carrot market connect error", e);
		}
		Element more = document.select("div.more-btn").stream().findFirst().orElseGet(() -> null);
		return ObjectUtils.isEmpty(more) ? 0 : Integer.parseInt(more.attr("data-total-pages"));
	}
}
