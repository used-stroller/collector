package team.three.usedstroller.collector.config;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
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

@Configuration
@RequiredArgsConstructor
public class ChromiumDriver extends BrowserDriver<ChromeDriver> {
	private final MyCollector myCollector;

	private String[] userAgents = {
			// IE
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)",
			"Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)",
			"Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US))",
			"Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 7.1; Trident/5.0)",
			"Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
			"Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; InfoPath.1; SV1; .NET CLR 3.8.36217; WOW64; en-US)",
			// Firefox
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; ro; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20120427 Firefox/15.0a1",
			"Mozilla/5.0 (Windows NT 6.1; rv:12.0) Gecko/20120403211507 Firefox/12.0",
			"Mozilla/5.0 (Windows NT 6.1; de;rv:12.0) Gecko/20120403211507 Firefox/12.0",
			"Mozilla/5.0 (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507 Firefox/12.0",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.16) Gecko/20120421 Gecko Firefox/11.0",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.16) Gecko/20120421 Firefox/11.0",
			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko Firefox/11.0",
			"Mozilla/5.0 (Windows NT 6.1; U;WOW64; de;rv:11.0) Gecko Firefox/11.0" };

	public void configureChromeDriver() {
		SecureRandom secureRandom = null;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			secureRandom = new SecureRandom();
		}

		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			System.setProperty(myCollector.getWebDriverId(), myCollector.getWinWebDriverPath());
		} else {
			System.setProperty(myCollector.getWebDriverId(), myCollector.getMacWebDriverPath());
		}

		options = new ChromeOptions();
		// headless(백그라운드 동작) 옵션 설정
		options.setHeadless(true);
		options.addArguments("--headless");
		options.addArguments("--no-sandbox");

		// 사람처럼 보이게 하는 옵션들
		options.addArguments("--disable-gpu"); // gpu 가속 비활성
		options.addArguments("lang=ko_KR");
		options.addArguments("user-agent="+userAgents[secureRandom.nextInt(userAgents.length)]); // 사용자 에이전트 랜덤 설정

		options.addArguments("--disable-notifications"); // 알림 비활성
		options.addArguments("--disable-extensions");
		options.addArguments("--disable-setuid-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--single-process");
		options.addArguments("--remote-allow-origins=*");

		// 웹 브라우저 프로필 설정
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0); // 팝업차단
		if (os.contains("win")) {
			prefs.put("download.default_directory", myCollector.getWinDownloadPath());
		} else {
			prefs.put("download.default_directory", myCollector.getMacDownloadPath());
		}
		prefs.put("download.prompt_for_download", false); // 다운로드 경로 묻지 않기
		options.setExperimentalOption("prefs", prefs);

		// 성능 로그 설정
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
	}

	@PostConstruct
	public void init() {
		configureChromeDriver();
		ChromeDriverService chromeDriverService = ChromeDriverService.createDefaultService();
		this.port = chromeDriverService.getUrl().getPort();
		this.driver = new ChromeDriver(chromeDriverService, options);
		this.driverWait = new WebDriverWait(this.driver, Duration.ofSeconds(5));
	}
}
