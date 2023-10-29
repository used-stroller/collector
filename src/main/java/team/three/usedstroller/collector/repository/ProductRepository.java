package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;

public interface ProductRepository extends JpaRepository<Product, Long> {

  boolean existsByPidAndSourceType(String pid, SourceType sourceType);
}
