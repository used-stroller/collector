package team.three.usedstroller.collector.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static team.three.usedstroller.collector.util.UnitConversionUtils.convertPrice;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "carrot")
public class Carrot extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 1000, nullable = false)
	private String title;

	private Long price;
	private String region;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(nullable = false)
	private String link;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String imgSrc;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String content;


	@Builder
	public Carrot(String title, String price, String region, String link, String imgSrc, String content) {
		this.title = title;
		this.price = convertPrice(price);
		this.region = region;
		this.link = link;
		this.imgSrc = imgSrc;
		this.content = content;
	}
}
