package team.three.usedstroller.collector.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PidDuplicationValidator {

  public static boolean isNotExistPid(ProductRepository productRepository, Product product) {
    if (productRepository.existsByPidAndSourceType(product.getPid(), product.getSourceType())) {
      log.info("{} [{}] is already exist", product.getSourceType(), product.getPid());
      return false;
    } else {
      return true;
    }
  }

  public static boolean isExistPid(ProductRepository productRepository, String pid, SourceType sourceType) {
    if (productRepository.existsByPidAndSourceType(pid, sourceType)) {
      log.info("{} [{}] is already exist", sourceType, pid);
      return true;
    } else {
      return false;
    }
  }

}
