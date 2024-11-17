package team.three.usedstroller.collector;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;
import team.three.usedstroller.collector.domain.entity.Product;

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
    List<Product> list = new ArrayList<>();

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
          Document detailDoc = null;
          String uploadTime = "";
          try {
            detailDoc = Jsoup.connect("https://www.daangn.com" + link).get();
            Element time = detailDoc.getElementsByTag("time").stream().findFirst()
                .orElseGet(() -> null);
            uploadTime = ObjectUtils.isEmpty(time) ? "" : time.text().replace("끌올", "");
          } catch (IOException e) {
            throw new RuntimeException("당근마켓 상세정보 가져오기 실패");
          }
          Product product = Product.createCarrot(title, price, region, link, imgSrc, content,
              uploadTime);
          list.add(product);
        });

    //then
    Assertions.assertThat(list).isNotNull().isNotEmpty();
    Assertions.assertThat(list).hasSize(12);
    System.out.println("list = " + list);
  }
}
