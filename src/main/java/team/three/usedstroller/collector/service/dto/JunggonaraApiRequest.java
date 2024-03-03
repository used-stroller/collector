package team.three.usedstroller.collector.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JunggonaraApiRequest {

  private Integer page;
  private Integer quantity;
  private String sort;
  private String searchWord;

  @Builder
  public JunggonaraApiRequest(Integer page, Integer quantity, String sort, String searchWord) {
    this.page = page;
    this.quantity = quantity;
    this.sort = sort;
    this.searchWord = searchWord;
  }
}