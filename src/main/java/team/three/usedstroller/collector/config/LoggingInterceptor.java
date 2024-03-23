package team.three.usedstroller.collector.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    log.debug("Sending request: [{}] {} {}", request.getMethod(), request.getURI(),
        new String(body, StandardCharsets.UTF_8));
    ClientHttpResponse response = execution.execute(request, body);
    log.debug("Received response: [{}] {}", response.getStatusCode(), response.getBody());
    return response;
  }
}
