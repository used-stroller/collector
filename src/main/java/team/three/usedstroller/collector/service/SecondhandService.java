package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.config.CustomWebDriver;
import team.three.usedstroller.collector.domain.BunJang;
import team.three.usedstroller.collector.domain.HelloMarket;
import team.three.usedstroller.collector.domain.Junggo;
import team.three.usedstroller.collector.repository.BunJangRepository;
import team.three.usedstroller.collector.repository.HelloMarketRepository;
import team.three.usedstroller.collector.repository.JunggonaraRepository;

import javax.script.ScriptException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class SecondhandService {
    private final JunggonaraRepository junggonaraRepository;
    private final BunJangRepository bunJangRepository;
    private final HelloMarketRepository helloMarketRepository;
    private final CustomWebDriver customWebDriver;

    public int collectingJunggonara() throws InterruptedException {
        int complete = 0;
        for (int i = 1; i < 2; i++) {
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
        }
        customWebDriver.closeBrower();
        return complete;
    }

    public int collectingBunJang() throws InterruptedException {
        int complete = 0;
        for (int i = 1; i < 2; i++) {
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
        }
        customWebDriver.closeBrower();
        return complete;
    }

    public int collectingHelloMarket() throws InterruptedException, ScriptException {
        int complete = 0;
        String url = "https://www.hellomarket.com/search?q=%EC%9C%A0%EB%AA%A8%EC%B0%A8";
        customWebDriver.openURL(url);
        scrollToTheBottom();
        WebElement content = customWebDriver.findElement(By.xpath("//*[@id=\"__next\"]"));
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
        String address="";
        String uploadTime;
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
            address = element.findElement(By.xpath("a/div[2]/div[2]/span[1]")).getText();
            String time = element.findElement(By.xpath("a/div[2]/div[2]/span[3]")).getText();
            uploadTime = convertToTimeFormat(time);

            Junggo junggo = Junggo.builder()
                    .title(title)
                    .link(link)
                    .price(price)
                    .imgSrc(img)
                    .address(address)
                    .uploadTime(uploadTime)
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
        String address;
        String uploadTime;
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
            address = element.findElement(By.xpath("a/div[3]")).getText();
            String time = element.findElement(By.xpath("a/div[2]/div[2]/div[2]/span")).getText();
            uploadTime = convertToTimeFormat(time);

            BunJang bunJang = BunJang.builder()
                    .title(title)
                    .link(link)
                    .price(price)
                    .imgSrc(img)
                    .address(address)
                    .uploadTime(uploadTime)
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
            }catch (Exception e){
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

    private static String getTime(WebElement element) {
        String time;
        try {
            element.findElement(By.xpath("div/div[2]/div[2]/div")).getText();
            time = element.findElement(By.xpath("div/div[2]/div[3]")).getText();
        }catch (Exception e) {
            time = element.findElement(By.xpath("div/div[2]/div[2]")).getText();
        }
        return time;
    }

    private static String convertToTimeFormat(String time){
        String exactTime="";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        if(time.contains("분")){
            String intStr = time.replaceAll("[^0-9]","");
            int i = Integer.parseInt(intStr);
            cal.add(Calendar.MINUTE, -i);
            exactTime= simpleDateFormat.format(cal.getTime());
        }
        if(time.contains("시간")){
            String intStr = time.replaceAll("[^0-9]","");
            int i = Integer.parseInt(intStr);
            cal.add(Calendar.HOUR, -i);
             exactTime= simpleDateFormat.format(cal.getTime());
        }
        if(time.contains("일")){
            String intStr = time.replaceAll("[^0-9]","");
            int i = Integer.parseInt(intStr);
            cal.add(Calendar.DATE, -i);
             exactTime= simpleDateFormat.format(cal.getTime());
        }
        if(time.contains("개월")){
            String intStr = time.replaceAll("[^0-9]","");
            int i = Integer.parseInt(intStr);
            cal.add(Calendar.MONTH, -i);
             exactTime= simpleDateFormat.format(cal.getTime());
        }
        if(time.contains("년")){
            String intStr = time.replaceAll("[^0-9]","");
            int i = Integer.parseInt(intStr);
            cal.add(Calendar.YEAR, -i);
             exactTime= simpleDateFormat.format(cal.getTime());
        }
        return exactTime;
    }

    private void scrollToTheBottom() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) customWebDriver.getDriver();

        long scrollHeight=0;
        long afterHeight=1;

        while(scrollHeight!=afterHeight) {
            scrollHeight = (long) js.executeScript("return document.body.scrollHeight"); //현재높이
            WebElement body = customWebDriver.findElement(By.tagName("body"));
            body.sendKeys(Keys.END);
            Thread.sleep(2000);
            afterHeight=(long) js.executeScript("return document.body.scrollHeight");; //스크롤한 뒤 페이지 높이
        }
    }
}
