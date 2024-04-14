package team.three.usedstroller.collector.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitConversionUtils {

  private static final String NOT_NUMBER = "\\D";

  public static String convertBunjangLink(String pid) {
    return "https://m.bunjang.co.kr/products/" + pid;
  }

  public static String convertJunggoLink(String pid) {
    return "https://web.joongna.com/product/" + pid;
  }

  public static String convertCarrotLink(String article) {
    return "https://www.daangn.com" + article;
  }

  public static String convertSecondwear(String pid) {
    return "https://www.hellomarket.com/item/" + pid;
  }

  public static String convertSimplePid(String link, String str) {
    if (!link.contains(str)) {
      return "";
    }
    return link.substring(link.lastIndexOf(str) + str.length());
  }

  public static long convertPricePart(String pricePart) {
    boolean isHundredMillion = pricePart.contains("억");
    boolean isTenThousand = pricePart.contains("만");
    boolean isThousand = pricePart.contains("천");
    String numberPart = pricePart.replaceAll("\\D", "");
    long number = Long.parseLong(numberPart);
    if (isHundredMillion) {
      return number * 100000000;
    } else if (isTenThousand) {
      return number * 10000;
    } else if (isThousand) {
      return number * 1000;
    } else {
      return number;
    }
  }

  public static Long convertPrice(String price) {
    String regex = "나눔|가격|연락|없음|중단";
    Pattern pattern = Pattern.compile(regex);
    if (ObjectUtils.isEmpty(price) || pattern.matcher(price).find()) {
      return 0L;
    }
    String[] priceParts = price.split("\\s+");
    long total = 0;
    for (String pricePart : priceParts) {
      total += convertPricePart(pricePart);
    }
    return total;
  }

  public static int changeInt(String releaseYear) {
    return ObjectUtils.isEmpty(releaseYear) ? 0 :
        Integer.parseInt(releaseYear.replaceAll(NOT_NUMBER, ""));
  }

  public static LocalDate convertLocalDate(Long updateTime) {
    return Instant.ofEpochSecond(updateTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  public static LocalDate convertLocalDateWithTimeStamp(Long updateTime) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    return LocalDate.parse(sdf.format(new Date(updateTime)));
  }

  public static LocalDate changeLocalDate(String uploadDate) {
    String pattern = uploadDate.contains("-") ? "yyyy-MM-dd" : "yyyy.MM.dd";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return ObjectUtils.isEmpty(uploadDate) ? null
        : LocalDate.parse(getDateText(uploadDate), formatter);
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
