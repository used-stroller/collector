package team.three.usedstroller.collector.service;

import java.util.List;
import java.util.Optional;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;

public interface ProductCollector {

  void start();

  Integer collectProduct();

  void deleteOldProducts(SourceType sourceType);

  /**
   * Product 리스트를 받아서 저장한다. 저장 성공 시 신규 저장된 갯수만 합산해서 반환한다.
   */
  default Integer saveProducts(ProductRepository repository, List<Product> products) {
    return products.stream()
        .map(product -> saveProduct(repository, product))
        .reduce(Integer::sum)
        .orElse(0);
  }

  /**
   * 1. 수집데이터가 DB에 존재하는지 조회. 2. 존재하면 동등 비교하여 값이 같으면 수집일자만 갱신 후 저장, 다르면 데이터 업데이트 후 저장. 3. 존재하지 않으면 신규
   * 저장 후 1을 반환.
   */
  default Integer saveProduct(ProductRepository repository, Product newProduct) {
    Optional<Product> dbProduct = repository.findByPidAndSourceType(newProduct.getPid(),
        newProduct.getSourceType());
    if (dbProduct.isPresent()) {
      Product oldProduct = dbProduct.get();
      boolean isEquals = oldProduct.equals(newProduct);
      if (isEquals) {
        oldProduct.updateDate();
        repository.save(oldProduct);
        return 0;
      }
      oldProduct.update(newProduct);
      repository.save(oldProduct);
    } else {
      repository.save(newProduct);
    }
    return 1;
  }

}
