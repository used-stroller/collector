package team.three.usedstroller.collector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.three.usedstroller.collector.domain.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

}
