package team.three.usedstroller.collector.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import team.three.usedstroller.collector.domain.Product;
import team.three.usedstroller.collector.domain.SourceType;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRes implements Serializable {

  private Long id;
  private SourceType sourceType;
  private String pid;
  private String title;
  private Long price;
  private String link;
  private String imgSrc;
  private String address;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedAt;

  //naver
  private int releaseYear;
  private String etc;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate uploadDate;

  //bunjang

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
