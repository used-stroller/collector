package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

}
