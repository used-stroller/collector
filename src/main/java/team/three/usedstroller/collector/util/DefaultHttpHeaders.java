package team.three.usedstroller.collector.util;

import java.security.SecureRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultHttpHeaders {

  public static final String[] USER_AGENTS = {
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
      //Windows 10-based PC using Edge browser
      "Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36",
      //Chrome OS-based laptop using Chrome browser (Chromebook)
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9",
      //Mac OS X-based computer using a Safari browser
      "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36",
      //Windows 7-based PC using a Chrome browser
      "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1"
      //Linux-based PC using a Firefox browser
  };

  public static String getUserAgent() {
    SecureRandom random = new SecureRandom();
    return USER_AGENTS[random.nextInt(USER_AGENTS.length)];
  }

  public static HttpHeaders getDefaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    headers.set("User-Agent", getUserAgent());
    return headers;
  }

}
