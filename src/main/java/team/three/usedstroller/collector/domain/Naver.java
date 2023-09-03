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
@Table(name = "naver_shopping")
public class Naver extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	private LocalDate uploadDate;
	private int releaseYear;

	@Column(length = 1000)
	private String etc;

	@Builder
	public Naver(String pid, String title, String link, String price, String imgSrc,
	             String uploadDate, String releaseYear, String etc) {
		this.pid = pid;
		this.title = title;
		this.link = link;
		this.price = convertPrice(price);
		this.imgSrc = imgSrc;
		this.uploadDate = changeLocalDate(uploadDate);
		this.releaseYear = changeInt(releaseYear);
		this.etc = etc;
	}

}
