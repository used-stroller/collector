package team.three.usedstroller.collector.domain.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NaverApiResponse {

  private Integer total;
  private Integer start;
  private Integer display;
  private List<Items> items;

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  public static class Items {

    private String title;
    private String link;
    private String image;
    private String lprice;
    private String hprice;
    private String productId;
    private String brand;

    public Long getPrice() {
      return Long.parseLong(lprice != null ? lprice : hprice);
    }
  }

}
