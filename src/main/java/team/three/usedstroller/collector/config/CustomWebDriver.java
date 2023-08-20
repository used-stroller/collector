package team.three.usedstroller.collector.config;

import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class CustomWebDriver {
    private WebDriver driver;

    public CustomWebDriver(){
        System.setProperty("webdriver.chrome.driver","C:/chromedriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver=new ChromeDriver(options);
    }
    public void openURL(String url){
        driver.get(url);
    }
    public void clickElementByCssSelector(String cssSelector){
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        element.click();
    }
    public void typeTextByCssSelector(String cssSelector,String text){
        WebElement element = driver.findElement(By.cssSelector(cssSelector));
        element.sendKeys(text);
    }

    public WebElement findElement(By by){
        WebElement element = driver.findElement(by);
        return element;
    }

    public List<WebElement> findElements(By by){
        List<WebElement> element = driver.findElements(by);
        return element;
    }
    public void closeBrower(){
        driver.close();//한개 탭만 종료
        driver.quit(); //webdriver 종료
    }
}
