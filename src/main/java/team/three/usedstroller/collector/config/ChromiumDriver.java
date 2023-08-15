package team.three.usedstroller.collector.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChromiumDriver extends BrowserDriver<ChromeDriver> {

	private final MyCollector myCollector;

	private String[] userAgents = {
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246", //Windows 10-based PC using Edge browser
		"Mozilla/5.0 (X11; CrOS x86_64 8172.45.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.64 Safari/537.36", //Chrome OS-based laptop using Chrome browser (Chromebook)
		"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9", //Mac OS X-based computer using a Safari browser
		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36", //Windows 7-based PC using a Chrome browser
		"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1" //Linux-based PC using a Firefox browser
	};

	/**
	 * headless(백그라운드 동작) 옵션 설정
	 */
	private void setHeadless() {
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-gpu");
	}

	/**
	 * 사람처럼 보이게 하는 옵션들
	 */
	private void setCustomOption() {
		SecureRandom secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			secureRandom = new SecureRandom();
		}
		options.addArguments("user-agent="+userAgents[secureRandom.nextInt(userAgents.length)]); // 사용자 에이전트 랜덤 설정

		options.addArguments("lang=ko_KR");
		options.addArguments("--disable-notifications"); // 알림 비활성
		options.addArguments("--disable-extensions"); // 확장 프로그램 비활성
		options.addArguments("--disable-setuid-sandbox"); // root 권한 무시
		options.addArguments("--single-process");
		options.addArguments("--remote-allow-origins=*"); // 크로스 도메인 허용
	}

	private void setByOs() {
		System.setProperty(myCollector.getWebDriverId(), myCollector.getPathMap().get("driverPath"));

		// 웹 브라우저 프로필 설정
		options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0); // 팝업차단
		prefs.put("download.default_directory", myCollector.getPathMap().get("downloadPath")); // 다운로드 경로 설정
		prefs.put("download.prompt_for_download", false); // 다운로드 경로 묻지 않기
		options.setExperimentalOption("prefs", prefs);

		log.info("WebDriver Path: {} / WebDownload Path: {}",
				myCollector.getPathMap().get("driverPath"),
				myCollector.getPathMap().get("downloadPath"));
	}

	public void chromeDriverLogging() {
		// 성능 로그 설정
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
	}

	@PostConstruct
	public void initChromeDriver() {
		setByOs();
		setHeadless();
		setCustomOption();
		chromeDriverLogging();
		this.driver = new ChromeDriver(options);
		this.driverWait = new WebDriverWait(this.driver, Duration.ofSeconds(5));
	}
}
