package team.three.usedstroller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UsedStrollerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void parseIntTest(){
		String qty = "18,025  개의 상품";
		String intStr = qty.replaceAll("[^0-9]", "");
		int i = Integer.parseInt(intStr);
		System.out.println("i = " + i);
	}

}
