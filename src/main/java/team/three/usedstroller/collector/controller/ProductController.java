package team.three.usedstroller.collector.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.three.usedstroller.collector.dto.FilterReq;
import team.three.usedstroller.collector.dto.PageRequest;
import team.three.usedstroller.collector.dto.ProductRes;
import team.three.usedstroller.collector.service.ProductService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
@Tag(name = "product", description = "상품 관련")
public class ProductController {

  private final ProductService productService;

  /**
   * 상품리스트 가져오기 Top10 브랜드, 모델리스트, 토탈카운트 포함 동네,가격,모델명,기간,브랜드,사이트별 파라미터 다 들어가는 한방 쿼리
   *
   * @param filter 제목, 수집처, 가격(min, max)
   * @param pageable 페이지, 사이즈, 정렬(예시: "price,desc,title,asc")
   */
  @GetMapping("/list")
  public Page<ProductRes> getProducts(FilterReq filter, PageRequest pageable) {
    return productService.getProducts(filter, pageable.of());
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
