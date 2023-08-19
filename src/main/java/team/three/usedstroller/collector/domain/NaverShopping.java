package team.three.usedstroller.collector.domain;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "naver_shopping")
public class NaverShopping extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 1000, nullable = false)
	private String title;

	private String price;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(nullable = false)
	private String link;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String imgSrc;

	private LocalDate uploadDate;
	private int releaseYear;

	@Column(length = 1000)
	private String etc;

	@Builder
	public NaverShopping(String title, String link, String price, String imgSrc,
	                     String uploadDate, String releaseYear, String etc) {
		this.title = title;
		this.link = link;
		this.price = price;
		this.imgSrc = imgSrc;
		this.uploadDate = changeLocalDate(uploadDate);
		this.releaseYear = changeInt(releaseYear);
		this.etc = etc;
	}

	private int changeInt(String releaseYear) {
		return ObjectUtils.isEmpty(releaseYear) ? 0 :
				Integer.parseInt(releaseYear.replaceAll("[^0-9]", ""));
	}

	private LocalDate changeLocalDate(String uploadDate) {
		return ObjectUtils.isEmpty(uploadDate) ? null :
				LocalDate.parse(uploadDate.length() < 9 ? uploadDate + "01" : uploadDate,
					DateTimeFormatter.ofPattern("yyyy.MM.dd"));
	}
}
