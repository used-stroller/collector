package team.three.usedstroller.collector.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JunggonaraItem {

  private Long seq; //pid 용도
  private String title;
  private Long price;
  private String url; // 이미지
  private String[] locationNames; // 주소
  private String sortDate; // 업로드 일시

  public String getLocation() {
    if (this.locationNames == null || this.locationNames.length == 0) {
      return null;
    }
    return this.locationNames[0];
  }

}