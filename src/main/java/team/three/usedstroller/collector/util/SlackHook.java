package team.three.usedstroller.collector.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackHook {

  @Value("${slack.url}")
  private String SLACK_WEBHOOK_URL;

  public void sendMessage(String channel, int count, double time) {
    RestTemplate restTemplate = new RestTemplate();
    String message = String.format("%s 완료: {%s}건, 수집 시간:{%s}s", channel, count, time);
    Map<String, Object> request = new HashMap<>();
    request.put("text", message); //전송할 메세지

    HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);
    restTemplate.exchange(SLACK_WEBHOOK_URL, HttpMethod.POST, entity, String.class);
  }
}
