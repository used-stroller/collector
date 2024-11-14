package team.three.usedstroller.collector.domain.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Locations {

  private List<Location> locations;

  @Data
  @NoArgsConstructor
  public static class Location {

    Integer id;
    String name1;
    String name2;
    String name3;
    String name;
    Integer name1Id;
    Integer name2Id;
    Long name3Id;
    Integer depth;
  }
}
