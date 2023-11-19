package team.three.usedstroller.collector.domain;

import static team.three.usedstroller.collector.util.UnitConversionUtils.changeInt;
import static team.three.usedstroller.collector.util.UnitConversionUtils.changeLocalDate;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertPid;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertPrice;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertSimplePid;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertToTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private SourceType sourceType;
  private String pid;
  @Column(length = 1000, nullable = false)
  private String title;
  private Long price;
  @Column(columnDefinition = "text")
  private String link;
  @Column(columnDefinition = "text")
  private String imgSrc;

  //naver
  private int releaseYear;
  @Column(length = 1000)
  private String etc;
  private LocalDate uploadDate;

  //bunjang
  private String address;

  //carrot
  private String region;
  @Column(columnDefinition = "text")
  private String content;

  @Builder
  private Product(SourceType sourceType, String pid, String title, String link, Long price,
      String imgSrc, String address,
      int releaseYear, String etc, LocalDate uploadDate, String region, String content) {
    this.sourceType = sourceType;
    this.pid = pid;
    this.title = title;
    this.link = link;
    this.price = price;
    this.imgSrc = imgSrc;
    this.address = address;
    this.releaseYear = releaseYear;
    this.etc = etc;
    this.uploadDate = uploadDate;
    this.region = region;
    this.content = content;
  }

  public static Product createNaver(String pid, String title, String link, String price,
      String imgSrc,
      String uploadDate, String releaseYear, String etc) {
    return Product.builder()
        .sourceType(SourceType.NAVER)
        .pid(pid)
        .title(title)
        .link(link)
        .price(convertPrice(price))
        .imgSrc(imgSrc)
        .uploadDate(changeLocalDate(uploadDate))
        .releaseYear(changeInt(releaseYear))
        .etc(etc)
        .build();
  }

  public static Product createBunJang(String title, String link, String price, String imgSrc,
      String address, String uploadTime) {
    return Product.builder()
        .sourceType(SourceType.BUNJANG)
        .pid(convertPid(link, "products/"))
        .title(title)
        .link(link)
        .price(convertPrice(price))
        .imgSrc(imgSrc)
        .address(address)
        .uploadDate(changeLocalDate(convertToTimeFormat(uploadTime)))
        .build();
  }

  public static Product createJunggo(String title, String link, String price, String imgSrc,
      String address, String uploadTime) {
    return Product.builder()
        .sourceType(SourceType.JUNGGO)
        .pid(convertSimplePid(link, "product/"))
        .title(title)
        .link(link)
        .price(convertPrice(price))
        .imgSrc(imgSrc)
        .address(address)
        .uploadDate(changeLocalDate(convertToTimeFormat(uploadTime)))
        .build();
  }

  public static Product createHelloMarket(String pid, String title, String link, String price,
      String imgSrc, String uploadTime) {
    return Product.builder()
        .sourceType(SourceType.HELLO)
        .pid(pid)
        .title(title)
        .link(link)
        .price(convertPrice(price))
        .imgSrc(imgSrc)
        .uploadDate(changeLocalDate(uploadTime))
        .build();
  }

  public static Product createCarrot(String title, String price, String region, String link,
      String imgSrc, String content) {
    return Product.builder()
        .sourceType(SourceType.CARROT)
        .pid(convertSimplePid(link, "/"))
        .title(title)
        .price(convertPrice(price))
        .region(region)
        .link(link)
        .imgSrc(imgSrc)
        .content(content)
        .build();
  }

}
