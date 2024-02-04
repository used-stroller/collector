package team.three.usedstroller.collector.util;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class ApiService {

  /**
   * webclient get 방식 호출
   * @param baseUrl 호출할 주소
   * @param responseRef 응답 객체 타입
   * @param responseType 응답 타입
   * @param useRedirect 서버의 리다이렉션을 따를지 결정
   * @return <T> 응답 객체 타입
   */
  public <T> Mono<T> apiCallGet(String baseUrl, ParameterizedTypeReference<T> responseRef,
      MediaType responseType, boolean useRedirect) {
    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
    factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
    SslContext sslContext = null;
    try {
      sslContext = SslContextBuilder
          .forClient()
          .trustManager(InsecureTrustManagerFactory.INSTANCE)
          .build();
    } catch (SSLException e) {
      throw new RuntimeException("SSL CONTEXT ERROR {} ", e);
    }

    SslContext finalSslContext = sslContext;
    HttpClient httpClient = HttpClient.create() // SslContext
        .secure(sslContextSpec -> sslContextSpec.sslContext(finalSslContext)
            .handshakeTimeout(Duration.ofMillis(360000)))
        .resolver(DefaultAddressResolverGroup.INSTANCE)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 360000) // Connection Timeout
        .doOnConnected(connection ->
            connection.addHandlerLast(
                    new ReadTimeoutHandler(360000, TimeUnit.MILLISECONDS)) // Read Timeout
                .addHandlerLast(
                    new WriteTimeoutHandler(360000, TimeUnit.MILLISECONDS))) // Write Timeout
        .resolver(DefaultAddressResolverGroup.INSTANCE);

    ClientHttpConnector connector = new ReactorClientHttpConnector(
        httpClient.followRedirect(useRedirect));
    WebClient wc = WebClient
        .builder()
        .clientConnector(connector)
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
        .uriBuilderFactory(factory)
        .baseUrl(baseUrl)
        .build();
    return wc.get()
        .accept(responseType)
        .retrieve()
        .bodyToMono(responseRef)
        .timeout(Duration.ofSeconds(300));
  }

}
