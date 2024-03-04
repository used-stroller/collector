package team.three.usedstroller.collector.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;

public interface ProductRepository extends JpaRepository<Product, Long> {

  boolean existsByPidAndSourceType(String pid, SourceType sourceType);
  Optional<Product> findByPidAndSourceType(String pid, SourceType sourceType);

  @Modifying
  void deleteAllBySourceTypeAndUpdatedAtIsBefore(SourceType sourceType, LocalDateTime updatedAt);
}
