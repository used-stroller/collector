package team.three.usedstroller.collector.util;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.three.usedstroller.collector.util.dto.SlackMessage;

@Service
@RequiredArgsConstructor
public class SlackHook {

  private final RestTemplate restTemplate;
  //  @Value("${slack.url}")
  private String slackToken;

  @PostConstruct
  public void init(Environment environment) {
    this.slackToken = environment.getProperty("slack.url");
  }

  public void sendMessage(String channel, int count, double time) {
    String message = String.format("%s 완료: {%s}건, 수집 시간:{%s}s", channel, count, time);
    Map<String, Object> request = new HashMap<>();
    request.put("text", message); //전송할 메세지

    HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(request);
    restTemplate.exchange(slackToken, HttpMethod.POST, entity, String.class);
  }

  public void sendSlackMessage(String message, String channel) {
    SlackMessage slackMessage = new SlackMessage(message, channel);
    HttpEntity<SlackMessage> entity = new HttpEntity<>(slackMessage);
    restTemplate.exchange(slackToken, HttpMethod.POST, entity, String.class);
  }
}
