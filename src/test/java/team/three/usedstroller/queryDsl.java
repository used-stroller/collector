package team.three.usedstroller;

import static team.three.usedstroller.collector.domain.QProduct.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import team.three.usedstroller.collector.domain.Product;

@DataJpaTest
@Import(TestQueryDslConfig.class)
@ActiveProfiles("local")
@AutoConfigureTestDatabase(replace = Replace.NONE) //실제 DB연결 해주는 설정, default가 내장형 DB
class queryDsl {

  @Autowired
  EntityManager em;


  @Autowired
  JPAQueryFactory queryFactory;

  @Test
  void findById() {
    Product productOne = queryFactory
        .selectFrom(product)
        .where(product.id.eq(1L))
        .fetchOne();
    System.out.println("productOne = " + productOne);

  }

}
