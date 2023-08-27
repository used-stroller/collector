package team.three.usedstroller.collector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import team.three.usedstroller.collector.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ChromedriverTest {

	RestTemplate restTemplate = new RestTemplate();
	private final Path RESOURCE_PATH = Paths.get("src/main/resources/libs/");


	@Test
	void download() throws IOException {
		//given
		clearDir();

		//when
		Document document = null;
		try {
			String URL = "https://googlechromelabs.github.io/chrome-for-testing/#stable";
			document = Jsoup.connect(URL).get();
		} catch (IOException e) {
			throw new RuntimeException("chromedriver web connect error", e);
		}

		//then
		Elements stable = document.select("#stable");
		String version = stable.select("p:contains(Version) > code:nth-child(1)").get(0).text();
		stable.select("td:contains(chromedriver)")
				.stream()
				.filter(el -> matchVersion(el, version))
				.forEach(this::downloadDrivers);
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

	private void clearDir() throws IOException {
		Files.walk(RESOURCE_PATH)
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.forEach(File::delete);
	}

	private static boolean matchVersion(Element element, String version) {
		String text = element.text();
		return text.contains(version) && (
				text.contains("linux64")
						|| text.contains("arm64")
						|| text.contains("win64")
		);
	}

}
