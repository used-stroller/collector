package team.three.usedstroller.collector.domain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CarrotTest {

	@DisplayName("총 페이지 수 크롤링")
	@Test
	void crawling_carrot_pages() throws IOException {
		//given
		String baseUrl = "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/";

		//when
		Document document = Jsoup.connect(baseUrl).get();
		Element more = document.select("div.more-btn")
				.stream().findFirst().orElseGet(() -> null);
		int pages = ObjectUtils.isEmpty(more) ? 0 : Integer.parseInt(more.attr("data-total-pages"));

		//then
		System.out.println("pages = " + pages);
		assertThat(pages).isPositive();
	}

	@DisplayName("한 페이지 크롤링 - 페이지당 12개 게시글 있음")
	@Test
	void crawling_carrot_page_content() throws IOException {
		//given
		String url = "https://www.daangn.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8/more/flea_market?page=";
		int page = 1;
		List<Carrot> list = new ArrayList<>();

		//when
		Document doc = Jsoup.connect(url + page).get();
		doc.select("article.flea-market-article")
				.forEach(element -> {
					String title = element.select("span.article-title").text();
					String content = element.select("span.article-content").text();
					String region = element.select("p.article-region-name").text();
					String price = element.select("p.article-price").text();
					String imgSrc = element.select("div.card-photo > img").attr("src");
					String link = element.select("a.flea-market-article-link").attr("href");
					Carrot carrot = Carrot.builder()
							.title(title)
							.content(content)
							.region(region)
							.price(price)
							.imgSrc(imgSrc)
							.link(link)
							.build();
					list.add(carrot);
				});

		//then
		assertThat(list).isNotNull().isNotEmpty();
		assertThat(list).hasSize(12);
	}
}
