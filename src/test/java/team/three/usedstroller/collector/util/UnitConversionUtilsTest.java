package team.three.usedstroller.collector.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UnitConversionUtilsTest {

  @ParameterizedTest
  @ValueSource(strings = {
      "142만 5000원", "12천 500원", "1억 3400만원", "500원",
      "120만원", "1000만원", "100만 500원", "나눔", "중단" })
  void convertToPrice(String price) {
    long result = UnitConversionUtils.convertPrice(price);
    System.out.println(price + " => " + result);
    Assertions.assertTrue(result >= 0);
  }

}