package team.three.usedstroller.collector.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("collector.crawler")
public class MyCollector {

	private String webDriverId;
	private String winWebDriverPath;
	private String winDownloadPath;
	private String macArmWebDriverPath;
	private String macDownloadPath;
	private String linuxWebDriverPath;
	private String linuxDownloadPath;

	private final Map<String, String> pathMap = new HashMap<>();

	@PostConstruct
	public void setPathByOs() {
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String webDriverPath = null;
		String webDownloadPath = null;

		if (osName.contains("Windows")) {
			webDriverPath = getResourcePath(winWebDriverPath);
			webDownloadPath = winDownloadPath;
		} else if (osName.contains("Mac")) {
			webDriverPath = getResourcePath(macArmWebDriverPath);
			webDownloadPath = macDownloadPath;
		} else if (osName.contains("Linux")) {
			webDriverPath = getResourcePath(linuxWebDriverPath);
			webDownloadPath = linuxDownloadPath;
		}

		pathMap.put("driverPath", webDriverPath);
		pathMap.put("downloadPath", webDownloadPath);
	}

	private static String getResourcePath(String path) {
		try {
			return new ClassPathResource(path).getURL().getPath();
		} catch (IOException e) {
			throw new RuntimeException("WebDriver 경로를 찾을 수 없습니다.");
		}
	}

}
