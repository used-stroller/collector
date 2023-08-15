package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.HelloMarket;
import team.three.usedstroller.collector.domain.Junggo;

public interface HelloMarketRepository extends JpaRepository<HelloMarket, Long> {
}
