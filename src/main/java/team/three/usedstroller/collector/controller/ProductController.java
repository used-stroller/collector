package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class ProductController {

  /**
   * 상품리스트 가져오기 Top10 브랜드, 모델리스트, 토탈카운트 포함 동네,가격,모델명,기간,브랜ㄷ,사이트별 파라미터 다 들어가는 한방 쿼리
   */

  @GetMapping("/list")
  public void getProductList() {
  }

  /**
   * 중모차 추천상품 리스트
   */
  @GetMapping("/list/recommend")
  public void getRecommandProductList() {
  }

  /**
   * 주소 api 호출
   */
  @GetMapping("/address/api")
  public void getAddress() {
  }

  /**
   * 브랜드별 모델리스트 파라미터 브랜드 id
   */
  @GetMapping("/model-list")
  public void getModelList() {
  }

  /**
   * 가격 min, max 리스트
   */
  @GetMapping("/price-range")
  public void getPriceRange() {
  }

}
