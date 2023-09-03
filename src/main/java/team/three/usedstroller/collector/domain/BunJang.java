package team.three.usedstroller.collector.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static team.three.usedstroller.collector.util.UnitConversionUtils.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BunJang extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String pid;
	@Column(length = 1000, nullable = false)
	private String title;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String link;
	private Long price;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String imgSrc;
	private String address;
	private String uploadTime;

	@Builder
	public BunJang(String title, String link, String price, String imgSrc,String address, String uploadTime) {
		this.pid = convertPid(link, "products/");
		this.title = title;
		this.link = link;
		this.price = convertPrice(price);
		this.imgSrc = imgSrc;
		this.address = address;
		this.uploadTime = convertToTimeFormat(uploadTime);
	}
}
