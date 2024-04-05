package team.three.usedstroller.collector.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SecondWearApiResponse {

  private Item result;
  private List<SecondWearItem> list;
  private Integer n;
  @JsonProperty("num_found")
  private Integer numFound;

  @Getter
  public class Item {

    private Integer totalCount;
  }

}
