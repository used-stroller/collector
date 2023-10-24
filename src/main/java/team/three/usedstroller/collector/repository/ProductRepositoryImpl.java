package team.three.usedstroller.collector.repository;

import static team.three.usedstroller.collector.domain.QProduct.product;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.dto.FilterReq;
import team.three.usedstroller.collector.dto.ProductRes;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements CustomProductRepository {

  private final JPAQueryFactory queryFactory;


  @Override
  public Page<ProductRes> getProducts(FilterReq filter, Pageable pageable) {

    ProductDsl<Product> productDsl = new ProductDsl<>(getSelectProduct(), filter);
    List<ProductRes> products = productDsl.getDsl() //
        .orderBy(product.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch()
        .stream()
        .map(ProductRes::of)
        .collect(Collectors.toList());

    ProductDsl<Long> countDsl = new ProductDsl<>(getSelectProductCount(), filter);

    return new PageImpl<>(products, pageable, countDsl.getDsl().fetchOne());
  }

  private JPAQuery<Product> getSelectProduct() {
    return queryFactory.select(product);
  }

  private JPAQuery<Long> getSelectProductCount() {
    return queryFactory.select(product.count());
  }
  
}
