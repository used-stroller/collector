package team.three.usedstroller.collector.util;

import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UnitConversionUtils {

	public UnitConversionUtils() {
	}

	public static Long changeCarrotPrice(String price) {
		if (ObjectUtils.isEmpty(price) || price.contains("나눔") || price.contains("가격없음")) {
			return 0L;
		} else {
			price = price.replaceAll("[,원]", "");
			if (price.contains("만")) {
				return Long.parseLong(price.replaceAll("[^0-9]", "")) * 10000;
			}
			return Long.parseLong(price);
		}
	}

	public static Long changeNaverPrice(String price) {
		return ObjectUtils.isEmpty(price) || price.contains("판매중단") ? 0L :
				Long.parseLong(price.replaceAll("[,원]", ""));
	}

	public static int changeInt(String releaseYear) {
		return ObjectUtils.isEmpty(releaseYear) ? 0 :
				Integer.parseInt(releaseYear.replaceAll("[^0-9]", ""));
	}

	public static LocalDate changeLocalDate(String uploadDate) {
		return ObjectUtils.isEmpty(uploadDate) ? null :
				LocalDate.parse(uploadDate.length() < 9 ? uploadDate + "01" : uploadDate,
						DateTimeFormatter.ofPattern("yyyy.MM.dd"));
	}
}
