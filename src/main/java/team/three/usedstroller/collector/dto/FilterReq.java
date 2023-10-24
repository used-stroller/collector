package team.three.usedstroller.collector.dto;

import java.util.List;
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
  private String town;
  private Integer price;
  private List<String> model;
  private String period;
  private List<String> brand;
  private SourceType sourceType;
  private Integer minPrice;
  private Integer maxPrice;
}
