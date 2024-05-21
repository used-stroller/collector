package team.three.usedstroller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import team.three.usedstroller.collector.domain.Keyword;
import team.three.usedstroller.collector.domain.Model;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.QProduct;
import team.three.usedstroller.collector.domain.dto.FilterReq;
import team.three.usedstroller.collector.repository.KeywordRepository;
import team.three.usedstroller.collector.repository.ModelRepository;
import team.three.usedstroller.collector.repository.ProductRepository;
import team.three.usedstroller.collector.util.UnitConversionUtils;

@SpringBootTest
@ActiveProfiles("local")
class UsedStrollerApplicationTests {

  @Autowired
  EntityManager em;
  JPAQueryFactory queryFactory;
  @Autowired
  KeywordRepository keywordRepository;
  @Autowired
  ModelRepository modelRepository;
  @Autowired
  ProductRepository productRepository;
  UnitConversionUtils unitConversionUtils;

  @BeforeEach
  public void init() {
    queryFactory = new JPAQueryFactory(em);
  }

  @Test
  void findById() {
    Product productOne = queryFactory
        .selectFrom(QProduct.product)
        .where(QProduct.product.id.eq(1L))
        .fetchOne();
    System.out.println("product = " + productOne);

  }

  @Test
  void findByAll() {
    List<Keyword> all = keywordRepository.findAll();
    System.out.println("all = " + all);

  }

  @Test
  @DisplayName("모델명리스트 조회")
  void getModelList() {
    modelRepository.findAll();
  }

  @Test
  @Commit
  @DisplayName("브랜드+모델명으로 조회하기")
  void getProductListByBrand() {
    List<String> brand = new ArrayList<>();
    brand.add("부가부");
    FilterReq filter = new FilterReq("비3", null, null, null, null, null, null, brand);
    List<Product> all = productRepository.getProductsOnly(filter);
    Model modelObj = modelRepository.findByName(filter.getKeyword());
    System.out.println("modelObj = " + modelObj);
    for (Product product : all) {
      product.setModel(modelObj);
      productRepository.save(product);
    }
  }

  @Test
  @DisplayName("ModelId가져오기")
  void getModelId() {
    List<String> brand = new ArrayList<>();
    brand.add("부가부");
    FilterReq filter = new FilterReq("비6", null, null, null, null, null, null, brand);
    String keyword = filter.getKeyword();
    System.out.println("keyword = " + keyword);
    Model b5 = modelRepository.findByName("비6");
    System.out.println("b5 = " + b5);
  }

  @Test
  @Commit
  @DisplayName("모델 업데이트 하기 ")
  void updateModel() {
    List<Model> modelList = modelRepository.findAll();
    for (int i = 0; i < 1; i++) {
      String brand = modelList.get(i).getBrand();
      String name = modelList.get(i).getName();
      List<String> brandList = new ArrayList<>();
      brandList.add(brand);
      FilterReq filter = new FilterReq(name, null, null, null, null, null, null, brandList);
      Model modelObj = modelRepository.findByName(name);
      List<Product> filteredList = productRepository.getProductsOnly(filter);
      for (int j = 0; j < filteredList.size(); j++) {
        filteredList.get(i).setModel(modelObj);
        productRepository.save(filteredList.get(i));
      }
    }
  }

  @Test
  void conversionDate() {
    String testStr = "\n"
        + "            끌올 2일 전\n"
        + "          ";

    String testStr2 = "\n"
        + "             6분 전\n"
        + "          ";

    String s = unitConversionUtils.convertToTimeFormat(testStr);
    System.out.println("s = " + s);
    LocalDate localDate = unitConversionUtils.changeLocalDate(s);
    System.out.println("localDate = " + localDate);
  }

//  @Test
//  void slackWebhook() {
//    SlackApi api = new SlackApi(
//        "https://hooks.slack.com/services/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");
//    api.call(new SlackMessage("my message"));
//  }


}
