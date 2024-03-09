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
public class BunjangApiResponse {

  private String result;
  private List<BunjangItem> list;
  private Integer n;
  @JsonProperty("num_found")
  private Integer numFound;

}
