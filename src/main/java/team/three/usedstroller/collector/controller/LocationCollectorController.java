package team.three.usedstroller.collector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.three.usedstroller.collector.service.LocationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/temp")
@Slf4j
public class LocationCollectorController {

  private final LocationService locationService;


  @GetMapping("/get/location")
  @ResponseStatus(HttpStatus.CREATED)
  public void getLocation() {
    locationService.getLocation();
  }

  @GetMapping("/save/location")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveLocation() throws JsonProcessingException, InterruptedException {
    locationService.saveLocation();
  }

}
