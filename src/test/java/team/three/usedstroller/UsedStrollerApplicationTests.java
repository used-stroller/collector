package team.three.usedstroller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.QProduct;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class UsedStrollerApplicationTests {

  @Autowired
  EntityManager em;
  JPAQueryFactory queryFactory;

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


}
