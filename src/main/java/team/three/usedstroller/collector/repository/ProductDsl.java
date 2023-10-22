package team.three.usedstroller.collector.repository;

import static team.three.usedstroller.collector.domain.QProduct.product;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.util.ObjectUtils;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.dto.FilterReq;

public class ProductDsl<T> {

  private JPAQuery<T> jpaQuery;

  public ProductDsl(JPAQuery<T> jpaQuery, FilterReq filter) {
    this.jpaQuery = jpaQuery
        .from(product);

    applyKeyword(filter.getKeyword());
    applySourceType(filter.getSourceType());
  }

  private void applySourceType(SourceType sourceType) {
    if (!ObjectUtils.isEmpty(sourceType)) {
      jpaQuery.where(product.sourceType.eq(sourceType));
    }
  }

  private void applyKeyword(String keyword) {
    if (keyword != null) {
      jpaQuery.where(product.title.containsIgnoreCase(keyword)
          .or(product.content.containsIgnoreCase(keyword))
          .or(product.etc.containsIgnoreCase(keyword)));
    }
  }

  public JPAQuery<T> getDsl() {
    return jpaQuery;
  }
}
