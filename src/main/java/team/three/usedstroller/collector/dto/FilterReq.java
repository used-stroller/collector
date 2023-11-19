package team.three.usedstroller.collector.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.three.usedstroller.collector.domain.SourceType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FilterReq {

  private String keyword;
  private SourceType sourceType;
  private Long minPrice;
  private Long maxPrice;
  private String town;
}
