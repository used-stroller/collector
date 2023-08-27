package team.three.usedstroller.collector.config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import team.three.usedstroller.collector.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Component
public class ChromeDriverDownloader {
	private final RestTemplate restTemplate = new RestTemplate();
	private final Path RESOURCE_PATH = Paths.get("src/main/resources/libs/");
	private final String CHROME_DRIVER_URL = "https://googlechromelabs.github.io/chrome-for-testing/#stable";

	public String updateLatestDriver() {
		clearDir(); // 기존 파일 삭제

		Document document = null;
		try {
			document = Jsoup.connect(CHROME_DRIVER_URL).get();
		} catch (
				IOException e) {
			throw new RuntimeException("chromedriver web connect error", e);
		}

		Elements stable = document.select("#stable");
		String version = stable.select("p:contains(Version) > code:nth-child(1)").get(0).text();
		stable.select("td:contains(chromedriver)")
				.stream()
				.filter(el -> matchVersion(el, version))
				.forEach(this::downloadDrivers);

		return version;
	}

	private void downloadDrivers(Element element) {
		String downloadUrl = element.text();
		String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);

		byte[] data = restTemplate.getForObject(downloadUrl, byte[].class);
		if (!ObjectUtils.isEmpty(data)) {
			try {
				Path path = Files.write(RESOURCE_PATH.resolve(fileName), data);
				ZipUtils.unzipFile(path.toFile(), RESOURCE_PATH.toString());
				path.toFile().delete();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void clearDir() {
		try {
			Files.walk(RESOURCE_PATH)
					.map(Path::toFile)
					.forEach(File::delete);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete an existing file", e);
		}
	}

	private static boolean matchVersion(Element element, String version) {
		String text = element.text();
		Pattern pattern = Pattern.compile("linux64|arm64|win64");
		return text.contains(version) && pattern.matcher(text).find();
	}
}
