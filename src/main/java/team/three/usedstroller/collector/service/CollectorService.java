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
import team.three.usedstroller.collector.config.CustomWebDriver;
import team.three.usedstroller.collector.domain.*;
import team.three.usedstroller.collector.repository.BunJangRepository;
import team.three.usedstroller.collector.repository.HelloMarketRepository;
import team.three.usedstroller.collector.repository.JunggonaraRepository;
import team.three.usedstroller.collector.repository.NaverShoppingRepository;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class CollectorService {


    private final ChromiumDriver driver;
    private final NaverShoppingRepository naverShoppingRepository;
    private final JunggonaraRepository junggonaraRepository;
    private final BunJangRepository bunJangRepository;
    private final HelloMarketRepository helloMarketRepository;
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

    public int collectingJunggonara() throws InterruptedException {
        int complete = 0;
        for (int i = 1; i < 200; i++) {
            String url = "https://web.joongna.com/search/%EC%9C%A0%EB%AA%A8%EC%B0%A8?page=" + i;
            customWebDriver.openURL(url);
            Thread.sleep(1000);
            try {
                WebElement content = customWebDriver.findElement(By.xpath("//*[@id=\"__next\"]/div/main/div[1]/div[2]/ul"));
                List<WebElement> list = content.findElements(By.xpath("li"));

                List<Junggo> itemList = getItemListJunggo(list);

                for (Junggo item : itemList) {
                    junggonaraRepository.save(item);
                    complete++;
                }
            } catch (Exception e) {
                break;
            }
            customWebDriver.closeBrower();
        }
        return complete;
    }

    public int collectingBunJang() throws InterruptedException {
        int complete = 0;
        for (int i = 1; i < 50; i++) {
            String url = "https://m.bunjang.co.kr/search/products?order=score&page=" + i + "&q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
            customWebDriver.openURL(url);
            Thread.sleep(1000);

            WebElement content = customWebDriver.findElement(By.cssSelector("#root"));
            List<WebElement> list = content.findElements(By.xpath("div/div/div[4]/div/div[4]/div/div"));

            List<BunJang> itemList = getItemListBunJang(list);
            for (BunJang item : itemList) {
                bunJangRepository.save(item);
                complete++;
            }

            customWebDriver.closeBrower();
        }
        return complete;
    }

    public int collectingHelloMarket() throws InterruptedException, ScriptException {
        int complete = 0;
        String url = "https://www.hellomarket.com/search?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
        customWebDriver.openURL(url);
        scrollToBottom(2000);

        WebElement content = customWebDriver.findElement(By.xpath("//*[@id=\"__next\"]"));
        System.out.println("content = " + content);
        List<WebElement> list = content.findElements(By.xpath("div[3]/div[3]/div[2]/div/div/div"));
        List<HelloMarket> itemList = getItemListHelloMarket(list);
        for (HelloMarket item : itemList) {
            helloMarketRepository.save(item);
            complete++;
        }

        //customWebDriver.closeBrower();
        return complete;
    }


    //============================================메서드=====================================================
    private static List<Junggo> getItemListJunggo(List<WebElement> list) {
        List<Junggo> junggoList = new ArrayList<>();
        String img;
        String price;
        String title;
        String link;
        for (WebElement element : list) {
            try {
                if (element.findElement(By.xpath("a/div[2]/h2")).getText() == null) {
                }
            } catch (Exception e) {
                continue;
            }
            title = element.findElement(By.xpath("a/div[2]/h2")).getText();
            link = element.findElement(By.xpath("a")).getAttribute("href");
            price = element.findElement(By.xpath("a/div[2]/div[1]")).getText();
            img = element.findElement(By.xpath("a/div[1]/img")).getAttribute("src");

            Junggo junggo = Junggo.builder()
                    .title(title)
                    .link(link)
                    .price(price)
                    .imgSrc(img)
                    .build();
            junggoList.add(junggo);
        }
        return junggoList;
    }

    private static List<BunJang> getItemListBunJang(List<WebElement> list) {
        List<BunJang> bunJangList = new ArrayList<>();
        String img;
        String price;
        String title;
        String link;
        for (WebElement element : list) {
            try {
                if (element.findElement(By.xpath("a")).getText() == null) {
                }
            } catch (Exception e) {
                continue;
            }
            title = element.findElement(By.xpath("a/div[2]/div[1]")).getText();
            link = element.findElement(By.xpath("a")).getAttribute("href");
            price = element.findElement(By.xpath("a/div[2]/div[2]/div[1]")).getText();
            img = element.findElement(By.xpath("a/div[1]/img")).getAttribute("src");

            BunJang bunJang = BunJang.builder()
                    .title(title)
                    .link(link)
                    .price(price)
                    .imgSrc(img)
                    .build();
            bunJangList.add(bunJang);
        }
        return bunJangList;
    }

    private static List<HelloMarket> getItemListHelloMarket(List<WebElement> list) {
        List<HelloMarket> helloMarketListList = new ArrayList<>();
        String img;
        String price;
        String title;
        String link;
        for (WebElement element : list) {
            try {
                if (element.findElement(By.xpath("div")).getText() == null) {
                }
            } catch (Exception e) {
                continue;
            }
            title = element.findElement(By.xpath("div/div[2]/a[2]/div")).getText();
            link = element.findElement(By.xpath("div/div[1]/a")).getAttribute("href");
            price = element.findElement(By.xpath("div/div[2]/a[1]/div")).getText();
            img = element.findElement(By.xpath("div/div[1]/a/img")).getAttribute("src");

            HelloMarket helloMarket = HelloMarket.builder()
                    .title(title)
                    .link(link)
                    .price(price)
                    .imgSrc(img)
                    .build();
            helloMarketListList.add(helloMarket);
        }
        return helloMarketListList;
    }

    private void scrollToBottom(int ms) throws InterruptedException {
        WebElement body = customWebDriver.findElement(By.tagName("body"));
        //boolean bottom = (boolean)isBottom();
        long time = new Date().getTime();
        while (new Date().getTime() < time + ms) {
            Thread.sleep(1000);
            body.sendKeys(Keys.END);
        }
    }


}
