package team.three.usedstroller;

import static team.three.usedstroller.collector.domain.QProduct.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import team.three.usedstroller.collector.domain.Product;

@DataJpaTest
@ActiveProfiles("local")
class queryDsl {

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
        .selectFrom(product)
        .where(product.id.eq(2L))
        .fetchOne();
    System.out.println("productOne = " + productOne);

  }

}
