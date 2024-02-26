package team.three.usedstroller.collector.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;

public interface ProductRepository extends JpaRepository<Product, Long> {

  boolean existsByPidAndSourceType(String pid, SourceType sourceType);

  @Modifying
  void deleteAllByCreatedAtIsBefore(LocalDateTime createdAt);
}
