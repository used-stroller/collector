package team.three.usedstroller.collector.config;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

@Slf4j
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    log.error("RestTemplate Error : [{}] {} {}", response.getStatusCode(), response.getHeaders(),
        response.getBody());
    super.handleError(response);
  }
}
