package team.three.usedstroller.collector.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import team.three.usedstroller.collector.domain.dto.carrot.CarrotDto;

@Slf4j
@Component
public class CarrotParser {

  public List<CarrotDto> parseScript(String url) {
    List<CarrotDto> dtoList = new ArrayList<>();
    try {
      Document doc = Jsoup.connect(url).get();
      Elements scriptElements = doc.select("script");

      for (Element scriptElement : scriptElements) {
        String scriptContent = scriptElement.html();

        // 스크립트 내용 중 'fleamarketArticles'를 포함하는지 확인
        if (scriptContent.contains("fleamarketArticles")) {
          JSONObject allPage = getScript(scriptContent);

          // fleamarketArticles 키의 값 추출
          if (allPage.has("fleamarketArticles")) {
            dtoList = getFleamarketArticles(allPage);
          } else {
            log.info("fleamarketArticles key not found.");
          }
          break; // 첫 번째 일치하는 스크립트만 찾으면 종료
        }
      }
    } catch (Exception e) {
      log.info("Error fetching the HTML: " + e.getMessage());
    }
    return dtoList;
  }

  private static JSONObject getScript(String scriptContent) throws JSONException {
    // 'window.__remixContext = ' 부분 제거하고, 실제 JSON 부분만 추출
    String jsonString = scriptContent.split("window.__remixContext = ")[1];
    jsonString = jsonString.substring(0, jsonString.lastIndexOf(";")).trim(); // ';' 제거

    // JSON 형식으로 파싱
    JSONObject jsonObject = new JSONObject(jsonString);

    // 중첩된 경로를 따라가기
    JSONObject loaderData = jsonObject.getJSONObject("state").getJSONObject("loaderData");
    JSONObject indexData = loaderData.getJSONObject("routes/kr.buy-sell._index");
    JSONObject allPage = indexData.getJSONObject("allPage");
    return allPage;
  }

  private static List<CarrotDto> getFleamarketArticles(JSONObject allPage)
      throws JSONException, JsonProcessingException {
    String fleamarketArticlesStr = allPage.getString("fleamarketArticles");
    JSONArray fleamarketArticles = new JSONArray(fleamarketArticlesStr);
    ObjectMapper objectMapper = new ObjectMapper();
    // JSONArray를 List<MyDto>로 변환
    List<CarrotDto> dtoList = objectMapper.readValue(fleamarketArticles.toString(),
        objectMapper.getTypeFactory().constructCollectionType(List.class, CarrotDto.class));
    return dtoList;
  }
}


