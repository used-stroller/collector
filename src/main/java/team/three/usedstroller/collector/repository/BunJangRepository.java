package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.BunJang;
import team.three.usedstroller.collector.domain.HelloMarket;

public interface BunJangRepository extends JpaRepository<BunJang, Long> {
}
