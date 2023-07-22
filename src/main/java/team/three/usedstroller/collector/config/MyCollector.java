package team.three.usedstroller.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("collector.crawler")
public class MyCollector {

	private String webDriverId;
	private String winWebDriverPath;
	private String winDownloadPath;
	private String macWebDriverPath;
	private String macDownloadPath;

}
