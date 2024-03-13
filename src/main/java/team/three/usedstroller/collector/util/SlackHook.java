package team.three.usedstroller.collector.util;

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import org.springframework.stereotype.Service;

@Service
public class SlackHook {

  public void sendMessage(String channel, int count, double time) {
    String message = String.format("%s 완료: {%s}건, 수집 시간: {%s}s", channel, count, time);
    SlackApi api = new SlackApi(
        "https://hooks.slack.com/services/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");
    api.call(new SlackMessage(message));
  }
}
