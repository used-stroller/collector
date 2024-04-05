package team.three.usedstroller.collector.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SecondWearItem {

  @JsonProperty("itemIdx")
  private String pid;
  private String title;
  @JsonProperty("linkUrl")
  private String link;
  private String price;
  @JsonProperty("imageUrl")
  private String imgSrc;
  @JsonProperty("timestamp")
  private Long uploadTime;
}
