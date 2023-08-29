package team.three.usedstroller.collector.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitConversionUtils {

	public static Long convertPrice(String price) {
		String regex = "나눔|가격|연락|없음|중단";
		Pattern pattern = Pattern.compile(regex);
		if (ObjectUtils.isEmpty(price) || pattern.matcher(price).find()) {
			return 0L;
		} else {
			price = price.replaceAll("[,원]", "");
			if (price.contains("만")) {
				return Long.parseLong(price.replaceAll("[^0-9]", "")) * 10000;
			}
			return Long.parseLong(price);
		}
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

	public static String convertToTimeFormat(String time) {
		String exactTime = "";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		if (time.contains("분")) {
			String intStr = time.replaceAll("[^0-9]", "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.MINUTE, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("시간")) {
			String intStr = time.replaceAll("[^0-9]", "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.HOUR, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("일")) {
			String intStr = time.replaceAll("[^0-9]", "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.DATE, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("개월")) {
			String intStr = time.replaceAll("[^0-9]", "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.MONTH, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("년")) {
			String intStr = time.replaceAll("[^0-9]", "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.YEAR, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		return exactTime;
	}

}
