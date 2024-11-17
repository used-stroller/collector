package team.three.usedstroller.collector.service.reactive;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import team.three.usedstroller.collector.domain.entity.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public abstract class CommonService {

  private final ProductRepository productRepository;
  private final ApplicationEventPublisher eventPublisher;

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
        .flatMap(this::saveItem)
        .onErrorResume(e -> Mono.error(new RuntimeException("Save Error!", e)))
        .reduce(Integer::sum);
  }

  public void deleteOldData(SourceType sourceType) {
    eventPublisher.publishEvent(sourceType);
  }
}
