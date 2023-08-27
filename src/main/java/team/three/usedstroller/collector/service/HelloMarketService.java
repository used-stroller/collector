package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.domain.HelloMarket;
import team.three.usedstroller.collector.repository.HelloMarketRepository;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

import static team.three.usedstroller.collector.util.UnitConversionUtils.convertToTimeFormat;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HelloMarketService {

    private final ChromiumDriver driver;
    private final HelloMarketRepository helloMarketRepository;

    public int collectingHelloMarket() throws InterruptedException, ScriptException {
        int complete = 0;
        String url = "https://www.hellomarket.com/search?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
        driver.open(url);
        scrollToTheBottom();
        WebElement content = driver.getXpath("//*[@id=\"__next\"]");
        List<WebElement> list = content.findElements(By.xpath("div[3]/div[3]/div[2]/div/div/div"));
        List<HelloMarket> itemList = getItemListHelloMarket(list);
        for (HelloMarket item : itemList) {
            helloMarketRepository.save(item);
            complete++;
        }

        driver.close();
        return complete;
    }

    private List<HelloMarket> getItemListHelloMarket(List<WebElement> list) {
        List<HelloMarket> helloMarketListList = new ArrayList<>();
        String img;
        String price;
        String title;
        String link;
        String uploadTime;


        for (WebElement element : list) {
            try {
                if (element.findElement(By.xpath("div")).getText() == null) {
                }
            } catch (Exception e) {
                continue;
            }
            try {
                title = element.findElement(By.xpath("div/div[2]/a[2]/div")).getText();
                link = element.findElement(By.xpath("div/div[1]/a")).getAttribute("href");
                price = element.findElement(By.xpath("div/div[2]/a[1]/div")).getText();
                img = element.findElement(By.xpath("div/div[1]/a/img")).getAttribute("src");
                String time = getTime(element); //element(무료배송)가 임의로 추가되는 경우 처리
                uploadTime = convertToTimeFormat(time);
            } catch (Exception e) {
                continue;
            }
            HelloMarket helloMarket = HelloMarket.builder()
                    .title(title)
                    .link(link)
                    .price(price)
                    .imgSrc(img)
                    .uploadTime(uploadTime)
                    .build();
            helloMarketListList.add(helloMarket);
        }
        return helloMarketListList;
    }

    private String getTime(WebElement element) {
        String time;
        try {
            element.findElement(By.xpath("div/div[2]/div[2]/div")).getText();
            time = element.findElement(By.xpath("div/div[2]/div[3]")).getText();
        } catch (Exception e) {
            time = element.findElement(By.xpath("div/div[2]/div[2]")).getText();
        }
        return time;
    }



    private void scrollToTheBottom() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver.driver;

        long scrollHeight = 0;
        long afterHeight = 1;

        while (scrollHeight != afterHeight) {
            scrollHeight = (long) js.executeScript("return document.body.scrollHeight"); //현재높이
            WebElement body = driver.getTag("body");
            body.sendKeys(Keys.END);
            Thread.sleep(2000);
            afterHeight = (long) js.executeScript("return document.body.scrollHeight");
        }
    }




}
