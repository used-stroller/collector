package team.three.usedstroller.collector.domain;

import static team.three.usedstroller.collector.util.UnitConversionUtils.changeLocalDate;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertBunjangLink;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertCarrotLink;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertJunggoLink;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertLocalDate;
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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import team.three.usedstroller.collector.domain.dto.BunjangItem;
import team.three.usedstroller.collector.domain.dto.JunggonaraItem;
import team.three.usedstroller.collector.domain.dto.NaverApiResponse;

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

  //carrot
  private String region;
  @Column(columnDefinition = "text")
  private String content;

  @Builder
  private Product(SourceType sourceType, String pid, String title, String link, Long price,
      String imgSrc,
      int releaseYear, String etc, LocalDate uploadDate, String region, String content) {
    this.sourceType = sourceType;
    this.pid = pid;
    this.title = title;
    this.link = link;
    this.price = price;
    this.imgSrc = imgSrc;
    this.releaseYear = releaseYear;
    this.etc = etc;
    this.uploadDate = uploadDate;
    this.region = region;
    this.content = content;
  }

  public static Product createNaver(NaverApiResponse.Items item) {
    return Product.builder()
        .sourceType(SourceType.NAVER)
        .pid(item.getProductId())
        .title(item.getTitle().replaceAll("<[^>]*>", ""))
        .link(item.getLink())
        .price(item.getPrice())
        .imgSrc(item.getImage())
        .etc(item.getBrand())
        .build();
  }

  public static Product createBunJang(BunjangItem item) {
    return Product.builder()
        .sourceType(SourceType.BUNJANG)
        .pid(item.getPid())
        .title(item.getName())
        .link(convertBunjangLink(item.getPid()))
        .price(Long.parseLong(item.getPrice().replaceAll("[^0-9]", "")))
        .imgSrc(item.getProductImage())
        .region(item.getLocation())
        .etc(item.getTag())
        .uploadDate(convertLocalDate(item.getUpdateTime()))
        .build();
  }

  public static Product createJunggo(JunggonaraItem item) {
    return Product.builder()
        .sourceType(SourceType.JUNGGO)
        .pid(item.getSeq().toString())
        .title(item.getTitle())
        .link(convertJunggoLink(item.getSeq().toString()))
        .price(item.getPrice())
        .imgSrc(item.getUrl())
        .region(item.getLocation())
        .uploadDate(changeLocalDate(item.getSortDate()))
        .build();
  }

  public static Product createSecondwear(String pid, String title, String link, String price,
      String imgSrc, String uploadTime) {
    return Product.builder()
        .sourceType(SourceType.SECOND)
        .pid(pid)
        .title(title)
        .link(link)
        .price(convertPrice(price))
        .imgSrc(imgSrc)
        .uploadDate(changeLocalDate(uploadTime))
        .build();
  }

  public static Product createCarrot(String title, String price, String region, String link,
      String imgSrc, String content, String uploadTime) {
    return Product.builder()
        .sourceType(SourceType.CARROT)
        .pid(convertSimplePid(link, "/"))
        .title(title)
        .price(convertPrice(price))
        .region(region)
        .link(convertCarrotLink(link))
        .imgSrc(imgSrc)
        .content(content)
        .uploadDate(changeLocalDate(convertToTimeFormat(uploadTime)))
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Product product = (Product) o;
    return releaseYear == product.releaseYear && sourceType == product.sourceType
        && Objects.equals(pid, product.pid) && Objects.equals(title, product.title)
        && Objects.equals(price, product.price) && Objects.equals(link, product.link)
        && Objects.equals(imgSrc, product.imgSrc) && Objects.equals(etc, product.etc)
        && Objects.equals(uploadDate, product.uploadDate)
        && Objects.equals(region, product.region) && Objects.equals(content, product.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceType, pid, title, price, link, imgSrc, releaseYear, etc,
        uploadDate, region, content);
  }

  public void update(Product newProduct) {
    super.updateData(newProduct);
    this.sourceType = newProduct.getSourceType();
    this.pid = newProduct.getPid();
    this.title = newProduct.getTitle();
    this.link = newProduct.getLink();
    this.price = newProduct.getPrice();
    this.imgSrc = newProduct.getImgSrc();
    this.releaseYear = newProduct.getReleaseYear();
    this.etc = newProduct.getEtc();
    this.uploadDate = newProduct.getUploadDate();
    this.region = newProduct.getRegion();
    this.content = newProduct.getContent();
  }

  public void updateDate() {
    super.updateDate();
  }
}
