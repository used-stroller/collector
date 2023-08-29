package team.three.usedstroller.collector.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static team.three.usedstroller.collector.util.UnitConversionUtils.convertPrice;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertToTimeFormat;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HelloMarket extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String link;
	private Long price;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String imgSrc;

	private String uploadTime;

	@Builder
	public HelloMarket(String title, String link, String price, String imgSrc, String uploadTime) {
		this.title = title;
		this.link = link;
		this.price = convertPrice(price);
		this.imgSrc = imgSrc;
		this.uploadTime = convertToTimeFormat(uploadTime);
	}
}
