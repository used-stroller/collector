package team.three.usedstroller.collector;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class UnitTest {

	@Test
	void year_month_parse() {
		//given
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

	@Test
	void change_price() {
		//given
		String before1 = "1,245,567원";
		String before2 = "나눔\uD83E\uDDE1"; //나눔🧡
		String before3 = "가격없음";

		//when
		String regex1 = "[,만원]";
		String regex2 = "[나눔\uD83E\uDDE1|가격없음]";

		String result1 = before1.replaceAll(regex1, "");
		long price = Long.parseLong(result1);
		String result2 = before2.replaceAll(regex2, "");
		System.out.println("result2 = " + result2);
		String result3 = before3.replaceAll(regex2, "");

		//then
		assertThat(price).isEqualTo(1245567);
		assertThat(result2).isEmpty();
		assertThat(result3).isEmpty();
	}

	@Test
	void regex() {
		//given
		String url1 = "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/116.0.5845.96/linux64/chromedriver-linux64.zip";
		String url2 = "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/116.0.5845.96/mac-arm64/chromedriver-mac-arm64.zip";
		String url3 = "https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/116.0.5845.96/win64/chromedriver-win64.zip";

		//when
		String regex = "linux64|arm64|win64";
		Pattern pattern = Pattern.compile(regex);
		Matcher linux = pattern.matcher(url1);
		Matcher macArm = pattern.matcher(url2);
		Matcher win = pattern.matcher(url3);

		//then
		if (linux.find()) {
			assertThat(linux.find()).isTrue();
			assertThat(linux.group()).isEqualTo("linux64");
		} else if (macArm.find()) {
			assertThat(macArm.find()).isTrue();
			assertThat(macArm.group()).isEqualTo("arm64");
		} else if (win.find()) {
			assertThat(win.find()).isTrue();
			assertThat(win.group()).isEqualTo("win64");
		}
	}

}