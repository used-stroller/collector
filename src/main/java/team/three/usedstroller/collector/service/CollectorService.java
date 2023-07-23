package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.ChromiumDriver;
import team.three.usedstroller.collector.config.CustomWebDriver;
import team.three.usedstroller.collector.domain.BunJang;
import team.three.usedstroller.collector.domain.NaverShopping;
import team.three.usedstroller.collector.repository.NaverShoppingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CollectorService {


    private final ChromiumDriver driver;
    private final NaverShoppingRepository naverShoppingRepository;
    private final CustomWebDriver customWebDriver;


    @Transactional
    public void collectingNaverShopping(String url) {
        driver.open(url);
        driver.wait(1);

        try {
            WebElement prodList = driver.get("#content > div.style_content__xWg5l > div.basicList_list_basis__uNBZx > div");
            List<WebElement> list = prodList.findElements(By.cssSelector("div"));
            String title = "";
            String link = "";
            String price = "";
            String brand = "";

            for (int i = 1; i < list.size(); i++) {
                String aClass = list.get(i).getDomAttribute("class");
                if (aClass == null) continue;
                if (aClass.contains("product_title") || aClass.contains("adProduct_title")) {
                    title = list.get(i).findElement(By.cssSelector("a")).getText();
                    link = list.get(i).findElement(By.cssSelector("a")).getAttribute("href");
                }
                if (aClass.contains("product_price_area") || aClass.contains("adProduct_price_area")) {
                    String priceClass = list.get(i).findElement(By.cssSelector("strong > span > span")).getDomAttribute("class");
                    if (priceClass == null) continue;
                    if (priceClass.contains("num")) {
                        price = list.get(i).findElement(By.cssSelector("strong > span > span")).getText();
                    }
                }
                if (aClass.contains("product_mall_area") || aClass.contains("adProduct_mall_area")) {
                    brand = list.get(i).findElement(By.cssSelector("a")).getText();
                }

                NaverShopping result = NaverShopping.builder()
                        .title(title)
                        .link(link)
                        .price(price)
                        .brand(brand)
                        .build();
                naverShoppingRepository.save(result);
                log.info("result = {}", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }

    }

    public void collectingNaver(String url) {
        driver.open(url);
        driver.wait(1);

        WebElement americaIndex = driver.get("#americaIndex");
        List<WebElement> list = americaIndex.findElements(By.className("point_up"));
        for (WebElement webElement : list) {
            log.info("title = {}", webElement.findElement(By.cssSelector(".tb_td2")).getText());
            log.info("price = {}", webElement.findElement(By.cssSelector(".tb_td3")).getText());
            log.info("rate = {}", webElement.findElement(By.cssSelector(".tb_td5")).getText());
        }

        driver.close();
    }

    public List<BunJang> collectingBunJang(String url) {
        List<BunJang> bunJangList = new ArrayList<>();
        customWebDriver.openURL(url);
        WebElement content = customWebDriver.findElement(By.cssSelector("#root"));
        List<WebElement> list = content.findElements(By.xpath("div/div/div[4]/div/div[4]/div/div"));
        String title = "";
        String link = "";
        String price = "";
        String img="";
        try {
            BunJang bunJang = new BunJang();
            for (WebElement element : list) {
                title = element.findElement(By.xpath("a/div[2]/div[1]")).getText();
                link = element.findElement(By.xpath("a")).getAttribute("href");
                price = element.findElement(By.xpath("a/div[2]/div[2]")).getText();
                img = element.findElement(By.xpath("a/div[1]/img")).getAttribute("src");
                log.info("title={}",title);
                log.info("link={}",link);
                log.info("price={}",price);
                bunJang.setLink(link);
                bunJang.setTitle(title);
                bunJang.setPrice(price);
                bunJang.setImg(img);
                bunJangList.add(bunJang);
            }
        }catch(Exception e){
          e.printStackTrace();
        }
         finally {
            //customWebDriver.closeBrower();
        }
        return bunJangList;
    }

}
