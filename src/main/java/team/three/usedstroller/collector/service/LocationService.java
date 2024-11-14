package team.three.usedstroller.collector.service;

import static team.three.usedstroller.collector.util.DefaultHttpHeaders.getDefaultHeaders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import team.three.usedstroller.collector.domain.Location;
import team.three.usedstroller.collector.domain.dto.Locations;
import team.three.usedstroller.collector.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

  private final LocationRepository locationRepository;
  private final RestTemplate restTemplate;

  public void getLocation() {
  }

  public void saveLocation() throws JsonProcessingException, InterruptedException {
    // 동 리스트 가져오기
    List<Location> all = locationRepository.findByCodeIsNull();

    // 동네코드 추출하기
    for (Location location : all) {
      String response = callApi(location.getThreeDepth());
      Thread.sleep(2000);
      Locations locations = parsingRespnose(response);
      for (Locations.Location loc : locations.getLocations()) {
        saveLocationTable(location, loc);
        if (location.getTwoDepth().equals(loc.getName2())) {
          break;  // 조건이 맞으면 루프 종료
        }
      }
    }
  }

  @Transactional
  public void saveLocationTable(Location location, Locations.Location loc) {
    if (location.getTwoDepth().equals(loc.getName2())) {
      Location saveEntity = Location.builder()
          .id(location.getId())
          .oneDepth(location.getOneDepth())
          .twoDepth(location.getTwoDepth())
          .threeDepth(location.getThreeDepth())
          .code(loc.getName3Id())
          .build();
      locationRepository.save(saveEntity);
    }
  }

  private Locations parsingRespnose(String response) throws JsonProcessingException {
    log.info("response ={}", response);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(response, Locations.class);
  }

  public String callApi(String threeDepth) {
    String url = "https://www.daangn.com/v1/api/search/kr/location?keyword=" + threeDepth;
    return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(getDefaultHeaders()),
        String.class).getBody();
  }
}
