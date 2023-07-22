package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.NaverShopping;

public interface NaverShoppingRepository extends JpaRepository<NaverShopping, Long> {
}
