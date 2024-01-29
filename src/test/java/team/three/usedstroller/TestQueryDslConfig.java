package team.three.usedstroller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import team.three.usedstroller.collector.config.ChromiumDriver;

@TestConfiguration
public class TestQueryDslConfig {

  @PersistenceContext
  private EntityManager em;

  ChromiumDriver driver;

  @Bean
  public JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(em);
  }


  @Test
  void convertTimeStampToDate() {
    String timeStamp = "1700282134829";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    Date date = new Date(Long.parseLong(timeStamp));
    String str = sdf.format(date);
    System.out.println("str = " + str);
  }

  @Test
  void jsonparseTest() {

  }


  void getLink(WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) driver.driver;
    element.click();
    driver.wait(1000);
    String currentUrl = driver.getCurrentUrl();
    System.out.println("currentUrl = " + currentUrl);
  }

  private void scrollToTheBottom() {
    JavascriptExecutor js = (JavascriptExecutor) driver.driver;

    long scrollHeight = 0;
    long afterHeight = 1;

    while (scrollHeight != afterHeight) {
      scrollHeight = (long) js.executeScript("return document.body.scrollHeight"); //현재높이
      WebElement body = driver.findOneByTag("body");
      body.sendKeys(Keys.END);
      driver.wait(2000);
      afterHeight = (long) js.executeScript("return document.body.scrollHeight");
    }
  }

  private void loginByKakao() {
    JavascriptExecutor js = (JavascriptExecutor) driver.driver;
    driver.open("https://www.hellomarket.com/auth?continue_url=/");
    String mainWindow = driver.getWindowHandle();
    WebElement kakaoLoginButton = driver.findOneByXpath("//*[@id=\"__next\"]/div/div[2]/div[1]");

    kakaoLoginButton.click();
    driver.wait(2000);
    Iterator<String> windowList = driver.driver.getWindowHandles().iterator();
    while (windowList.hasNext()) {
      String childWindow = windowList.next();
      driver.driver.switchTo().window(childWindow);
    }
  }
}
