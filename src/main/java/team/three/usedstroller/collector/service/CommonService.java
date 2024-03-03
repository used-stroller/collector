package team.three.usedstroller.collector.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public abstract class CommonService {

  private final ProductRepository productRepository;

  public Mono<Integer> saveItem(Product newProduct) {
    Optional<Product> dbProduct = productRepository.findByPidAndSourceType(
        newProduct.getPid(), newProduct.getSourceType());
    if (dbProduct.isPresent()) {
      Product oldProduct = dbProduct.get();
      boolean isEquals = oldProduct.equals(newProduct);
      if (isEquals) {
        oldProduct.updateDate();
        productRepository.save(oldProduct);
        return Mono.just(0);
      }
      oldProduct.update(newProduct);
      productRepository.save(oldProduct);
    } else {
      productRepository.save(newProduct);
    }
    return Mono.just(1);
  }

  public Mono<Integer> saveProducts(List<Product> items) {
    return Flux.fromIterable(items)
        .publishOn(Schedulers.boundedElastic())
        .flatMap(this::saveItem)
        .onErrorResume(e -> Mono.error(new RuntimeException("Save Error!", e)))
        .reduce(Integer::sum);
  }
}
