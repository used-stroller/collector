package team.three.usedstroller.collector.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRes {

  private Long id;
  private SourceType sourceType;
  private String pid;
  private String title;
  private Long price;
  private String link;
  private String imgSrc;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  //naver
  private int releaseYear;
  private String etc;
  private LocalDate uploadDate;

  //bunjang
  private String address;

  //carrot
  private String region;
  private String content;

  @Builder
  private ProductRes(Long id, String sourceType, String pid, String title, Long price,
      String link, String imgSrc, LocalDateTime createdAt, LocalDateTime updatedAt, int releaseYear,
      String etc, LocalDate uploadDate, String address, String region, String content) {
    this.id = id;
    this.sourceType = SourceType.valueOf(sourceType);
    this.pid = pid;
    this.title = title;
    this.price = price;
    this.link = link;
    this.imgSrc = imgSrc;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.releaseYear = releaseYear;
    this.etc = etc;
    this.uploadDate = uploadDate;
    this.address = address;
    this.region = region;
    this.content = content;
  }

  public static ProductRes of(Product product) {
    return ProductRes.builder()
        .id(product.getId())
        .sourceType(product.getSourceType().name())
        .pid(product.getPid())
        .title(product.getTitle())
        .price(product.getPrice())
        .link(product.getLink())
        .imgSrc(product.getImgSrc())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .releaseYear(product.getReleaseYear())
        .etc(product.getEtc())
        .uploadDate(product.getUploadDate())
        .address(product.getAddress())
        .region(product.getRegion())
        .content(product.getContent())
        .build();
  }
}
