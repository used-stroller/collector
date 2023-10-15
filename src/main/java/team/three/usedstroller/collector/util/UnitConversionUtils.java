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

	private static final String NOT_NUMBER = "\\D";

	public static String convertPid(String link, String str) {
		if (!link.contains(str)) {
			return "";
		}
		int start = link.indexOf(str) + str.length();
		int end = link.indexOf("?");
		return link.substring(start, end);
	}

	public static String convertSimplePid(String link, String str) {
		if (!link.contains(str)) {
			return "";
		}
		return link.substring(link.lastIndexOf(str) + str.length());
	}

	public static Long convertPrice(String price) {
		String regex = "나눔|가격|연락|없음|중단";
		Pattern pattern = Pattern.compile(regex);
		if (ObjectUtils.isEmpty(price) || pattern.matcher(price).find()) {
			return 0L;
		} else {
			if (price.contains("만원")) {
				return Long.parseLong(price.replaceAll(NOT_NUMBER, "")) * 10000;
			}
			if (price.contains("억") || price.contains("억원")) {
				return Long.parseLong(price.replaceAll(NOT_NUMBER, "")) * 100000000;
			}
			price = price.replaceAll("[,억만원]", "");
			return Long.parseLong(price);
		}
	}

	public static int changeInt(String releaseYear) {
		return ObjectUtils.isEmpty(releaseYear) ? 0 :
				Integer.parseInt(releaseYear.replaceAll(NOT_NUMBER, ""));
	}

	public static LocalDate changeLocalDate(String uploadDate) {
		String pattern = uploadDate.contains("-") ? "yyyy-MM-dd" : "yyyy.MM.dd";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return ObjectUtils.isEmpty(uploadDate) ? null : LocalDate.parse(getDateText(uploadDate),  formatter);
	}

	private static String getDateText(String uploadDate) {
		return uploadDate.length() < 9 ? uploadDate + "01" :
			uploadDate.length() > 11 ? uploadDate.substring(0, 10) : uploadDate;
	}

	public static String convertToTimeFormat(String time) {
		String exactTime = "";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		if (time.contains("분")) {
			String intStr = time.replaceAll(NOT_NUMBER, "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.MINUTE, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("시간")) {
			String intStr = time.replaceAll(NOT_NUMBER, "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.HOUR, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("일")) {
			String intStr = time.replaceAll(NOT_NUMBER, "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.DATE, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("개월")) {
			String intStr = time.replaceAll(NOT_NUMBER, "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.MONTH, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		if (time.contains("년")) {
			String intStr = time.replaceAll(NOT_NUMBER, "");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.YEAR, -i);
			exactTime = simpleDateFormat.format(cal.getTime());
		}
		return exactTime;
	}

}
