package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.Carrot;

public interface CarrotRepository extends JpaRepository<Carrot, Long> {
}
