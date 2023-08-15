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
			// Firefox
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; ro; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20120427 Firefox/15.0a1",
			"Mozilla/5.0 (Windows NT 6.1; rv:12.0) Gecko/20120403211507 Firefox/12.0",
			"Mozilla/5.0 (Windows NT 6.1; de;rv:12.0) Gecko/20120403211507 Firefox/12.0",
			"Mozilla/5.0 (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507 Firefox/12.0",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.16) Gecko/20120421 Gecko Firefox/11.0",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.16) Gecko/20120421 Firefox/11.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko Firefox/11.0",
			"Mozilla/5.0 (Windows NT 6.1; U;WOW64; de;rv:11.0) Gecko Firefox/11.0"
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
