package team.three.usedstroller.collector.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofMillis(2000)) // 서버 연결 대기 시간
        .setReadTimeout(Duration.ofMillis(3000)) // 서버 응답 대기 시간
        .interceptors(new LoggingInterceptor()) // 디버깅용 로깅 인터셉터 추가
        .errorHandler(new RestTemplateErrorHandler()) // 에러 처리 인터셉터 추가
        .build();
  }

}
