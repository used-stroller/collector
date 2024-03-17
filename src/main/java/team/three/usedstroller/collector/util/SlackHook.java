package team.three.usedstroller.collector.util;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackHook {

  @Value("${slack.url}")
  private String slackUrl;

  public void sendMessage(String channel, int count, double time) {
    String message = String.format("%s 완료: {%s}건, 수집 시간: {%s}s", channel, count, time);
    SlackApi api = new SlackApi(slackUrl);
    api.call(new SlackMessage(message));
  }
}
