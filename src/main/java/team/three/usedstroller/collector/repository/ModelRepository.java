package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.entity.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {

  Model findByName(String name);
}
