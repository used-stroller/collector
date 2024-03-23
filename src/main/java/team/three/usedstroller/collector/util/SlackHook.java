package team.three.usedstroller.collector.util;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.three.usedstroller.collector.util.dto.SlackMessage;

@Service
@RequiredArgsConstructor
public class SlackHook {

  private final RestTemplate restTemplate;
  @Value("${slack.url}")
  private String SLACK_WEBHOOK_URL;

  public void sendMessage(String channel, int count, double time) {
    String message = String.format("%s 완료: {%s}건, 수집 시간:{%s}s", channel, count, time);
    Map<String, Object> request = new HashMap<>();
    request.put("text", message); //전송할 메세지

    HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);
    restTemplate.exchange(SLACK_WEBHOOK_URL, HttpMethod.POST, entity, String.class);
  }

  public void sendSlackMessage(String message, String channel) {
    SlackMessage slackMessage = new SlackMessage(message, channel);
    HttpEntity<SlackMessage> entity = new HttpEntity<>(slackMessage);
    restTemplate.exchange(SLACK_WEBHOOK_URL, HttpMethod.POST, entity, String.class);
  }
}
