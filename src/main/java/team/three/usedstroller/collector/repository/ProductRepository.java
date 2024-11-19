package team.three.usedstroller.collector.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {

  boolean existsByPidAndSourceType(String pid, SourceType sourceType);

  Optional<Product> findByPidAndSourceType(String pid, SourceType sourceType);

  List<Product> findBySourceTypeAndUploadDateIsNull(SourceType sourceType);

  @Modifying
  void deleteAllBySourceTypeAndUpdatedAtIsBefore(SourceType sourceType, LocalDateTime updatedAt);
}
