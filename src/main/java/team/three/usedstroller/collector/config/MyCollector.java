package team.three.usedstroller.collector.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MyCollector {

	@Value("${collector.crawler.web-driver-id}")
	private String webDriverId;

	@Value("${collector.crawler.win-web-driver-path}")
	private String winWebDriverPath;

	@Value("${collector.crawler.win-download-path}")
	private String winDownloadPath;

	@Value("${collector.crawler.mac-web-driver-path}")
	private String macWebDriverPath;

	@Value("${collector.crawler.mac-download-path}")
	private String macDownloadPath;

}
