package team.three.usedstroller.collector.domain.dto.carrot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarrotDto {

  String thumbnail;
  @JsonProperty("regionId")
  Region regionId;
  String price;
  String id;
  String href;
  String title;
  String content;
  String status; //Ongoing

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Region {

    Long dbId; //6030
    String name; //개포동
  }
}