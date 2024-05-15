package team.three.usedstroller.collector.repository;

import static team.three.usedstroller.collector.domain.QProduct.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.dto.FilterReq;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements CustomProductRepository {

  private final JPAQueryFactory query;

  @Override
  public List<Product> getProductsOnly(FilterReq filter) {
    JPAQuery<Product> jpaQuery = query.selectFrom(product)
        .where(
            applyKeyword(filter.getKeyword()),
            applyBrand(filter.getBrand())
        );
    List<Product> products = jpaQuery
        .fetch()
        .stream()
        .toList();
    return products;
  }

  private BooleanExpression applyBrand(List<String> brand) {
    if (!CollectionUtils.isEmpty(brand)) {
      return brand.stream()
          .map(product.title::containsIgnoreCase)
          .reduce(BooleanExpression::or)
          .orElse(null);
    }
    return null;
  }

  private BooleanExpression applyKeyword(String keyword) {
    if (StringUtils.hasText(keyword)) {
      return product.title.containsIgnoreCase(keyword)
          .or(product.content.containsIgnoreCase(keyword))
          .or(product.etc.containsIgnoreCase(keyword));
    }
    return null;
  }
}
