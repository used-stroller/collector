package team.three.usedstroller.collector;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

class TimeTest {

	@Test
	void getTime(){
		String exam0 = "38분 전";
		String exam1 = "7시간 전";
		String exam2 = "1일 전";
		String exam3 = "3개월 전";
		String exam4 = "1년 전";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		if(exam4.contains("분")){
			String intStr = exam4.replaceAll("[^0-9]","");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.MINUTE, -i);
			String before7= simpleDateFormat.format(cal.getTime());
			System.out.println("분 = " + before7);
		}
		if(exam4.contains("시간")){
			String intStr = exam4.replaceAll("[^0-9]","");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.HOUR, -i);
			String before7= simpleDateFormat.format(cal.getTime());
			System.out.println("시간 = " + before7);
		}
		if(exam4.contains("일")){
			String intStr = exam4.replaceAll("[^0-9]","");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.DATE, -i);
			String before7= simpleDateFormat.format(cal.getTime());
			System.out.println("일 = " + before7);
		}
		if(exam4.contains("개월")){
			String intStr = exam4.replaceAll("[^0-9]","");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.MONTH, -i);
			String before7= simpleDateFormat.format(cal.getTime());
			System.out.println("개월 = " + before7);
		}
		if(exam4.contains("년")){
			String intStr = exam4.replaceAll("[^0-9]","");
			int i = Integer.parseInt(intStr);
			cal.add(Calendar.YEAR, -i);
			String before7= simpleDateFormat.format(cal.getTime());
			System.out.println("년 = " + before7);
		}

	}
}
