package team.three.usedstroller.collector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.three.usedstroller.collector.config.ChromeDriverDownloader;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

  private final ChromeDriverDownloader chromeDriverDownloader;

  @PostMapping("/driver-update")
  @ResponseStatus(HttpStatus.CREATED)
  public void downloadChromeDriver() {
    chromeDriverDownloader.updateLatestDriver();
  }

}
