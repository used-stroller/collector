package team.three.usedstroller.collector.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BunjangItem {

  private String pid;
  private String name;
  private String price;
  @JsonProperty("product_image")
  private String productImage;
  private String location;
  private String tag;
  @JsonProperty("update_time")
  private Long updateTime;
  private boolean ad;

}
