package team.three.usedstroller.collector.domain;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
public class BunJang {

	private String title;
	private String link;
	private String price;
	private String img;

}
