package team.three.usedstroller.collector.config;


import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public abstract class BrowserDriver<T extends ChromeDriver> {

  public T driver;
  public WebDriverWait driverWait;
  public ChromeOptions options;

  /**
   * 페이지 열기
   */
  public void open(String url) {
    try {
      log.info("Chrome Open URL : {}", url);
      this.driver.manage().timeouts().implicitlyWait(Duration.ofMillis(100));
      this.driver.get(url);
      this.driver.manage().window().maximize();
    } catch (Exception e) {
      throw new RuntimeException("Chrome Open Error", e);
    }
  }

  /**
   * Selector가 로드 됐을 때 불러오기
   */
  public WebElement findOneByCss(String selector) {
    WebElement element = null;
    try {
      element = driverWait.until(
          ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
    } catch (WebDriverException e) {
      log.error("{} 오브젝트를 불러오는데 실패했습니다.", selector);
    }
    return element;
  }

  /**
   * Selector가 로드 됐을 때 리스트로 불러오기
   */
  public List<WebElement> findAllByCss(String selector) {
    List<WebElement> elements = null;
    try {
      elements = driverWait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(selector)));
    } catch (WebDriverException e) {
      log.error("{} 오브젝트를 불러오는데 실패했습니다.", selector);
    }
    return elements;
  }

  /**
   * Xpath가 로드 됐을 때 불러오기
   */
  public WebElement findOneByXpath(String xPath) {
    WebElement element = null;
    try {
      element = driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
    } catch (WebDriverException e) {
      log.error("{} 오브젝트를 불러오는데 실패했습니다.", xPath);
    }
    return element;
  }

  /**
   * Xpath가 로드 됐을 때 리스트로 불러오기
   */
  public List<WebElement> findAllByXpath(String xPath) {
    List<WebElement> element = null;
    try {
      element = driverWait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xPath)));
    } catch (WebDriverException e) {
      log.error("{} 오브젝트를 불러오는데 실패했습니다.", xPath);
    }
    return element;
  }

  /**
   * Tag가 로드 됐을 때 불러오기
   */
  public WebElement findOneByTag(String tagName) {
    WebElement element = null;
    try {
      element = driverWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(tagName)));
    } catch (WebDriverException e) {
      log.error("{} 오브젝트를 불러오는데 실패했습니다.", tagName);
    }
    return element;
  }

  /**
   * 탭 닫기
   */
  public void close() {
    if (driver != null) {
      driver.close();
    }
  }

  /**
   * 브라우저 종료
   */
  public void quit() {
    if (driver != null) {
      driver.quit();
    }
  }

  /**
   * 뒤로가기
   */
  public void back() {
    driver.navigate().back();
  }

  /**
   * iframe에서 => 원래 html으로 복귀
   */
  public void switchToDefault() {
    driver.switchTo().defaultContent();
  }

  /**
   * main frame값 가져오기
   */
  public String getWindowHandle() {
    return driver.getWindowHandle();
  }


  /**
   * 현재 url주소 가져오기
   */
  public String getCurrentUrl() {
    String url = driver.getCurrentUrl();
    return url;
  }


  /**
   * 암묵적 대기 설정: 지정 시간동안 대기하며, 요소가 나타나면 즉시 진행
   */
  public void implicitWait(Duration duration) {
    driver.manage().timeouts().implicitlyWait(duration);
  }


  /**
   * 일정 시간 대기
   */
  public void wait(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }

}
