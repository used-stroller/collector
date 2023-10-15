package team.three.usedstroller.collector.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

import static team.three.usedstroller.collector.util.UnitConversionUtils.*;

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
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(nullable = false)
	private String link;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
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
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;

	@Builder
	private Product(SourceType sourceType, String pid, String title, String link, Long price, String imgSrc, String address,
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

	public static Product createNaver(String pid, String title, String link, String price, String imgSrc,
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

	public static Product createBunJang(String title, String link, String price, String imgSrc, String address, String uploadTime) {
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

	public static Product createJunggo(String title, String link, String price, String imgSrc,String address,String uploadTime) {
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

	public static Product createHelloMarket(String title, String link, String price, String imgSrc, String uploadTime) {
		return Product.builder()
			.sourceType(SourceType.HELLO)
			.pid(convertPid(link, "item/"))
			.title(title)
			.link(link)
			.price(convertPrice(price))
			.imgSrc(imgSrc)
			.uploadDate(changeLocalDate(convertToTimeFormat(uploadTime)))
			.build();
	}

	public static Product createCarrot(String title, String price, String region, String link, String imgSrc, String content) {
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
