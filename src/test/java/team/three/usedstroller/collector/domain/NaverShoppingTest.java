package team.three.usedstroller.collector.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class NaverShoppingTest {

	@Test
	void year_month_parse() {
		//given
		String uploadDate = "2023.08.01";

		//when
		LocalDate parse1 = LocalDate.parse("2023-08-01");
		LocalDate parse2 = LocalDate.parse("2023.08.01", DateTimeFormatter.ofPattern("yyyy.MM.dd"));

		//then
		assertThat(parse1).isEqualTo(LocalDate.of(2023, 8, 1));
		assertThat(parse2).isEqualTo(LocalDate.of(2023, 8, 1));
	}

	@Test
	void change_int() {
		//given
		String releaseYear = "2023년도";

		//when
		String regex = "[^0-9]";
		String result = releaseYear.replaceAll(regex, "");
		int year = Integer.parseInt(result);

		//then
		assertThat(year).isEqualTo(2023);
	}

}