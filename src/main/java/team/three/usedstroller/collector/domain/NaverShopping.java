package team.three.usedstroller.collector.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "naver_shopping")
public class NaverShopping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String link;
	private String price;
	private String brand;

	@Builder
	public NaverShopping(String title, String link, String price, String brand) {
		this.title = title;
		this.link = link;
		this.price = price;
		this.brand = brand;
	}
}
