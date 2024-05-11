package team.three.usedstroller.collector.service.mvc;

import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

  private final BunJangServiceMvc bunJangServiceMvc;
  private final CarrotServiceMvc carrotServiceMvc;
  private final JunggonaraServiceMvc junggonaraServiceMvc;
  private final SecondWearServiceMvc secondWearServiceMvc;
  private final NaverServiceMvc naverServiceMvc;

  public void collectAll() {
    ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 시간이 오래걸리는 당근, 중고나라는 쓰레드로 처리
     */
    collectCarrotAndJunggoByTread(executorService);

    /**
     * 번개장터,세컨웨어,네이버는 순차수집
     */
    try {
      log.info("=================세컨웨어 수집 시작됨===================");
      secondWearServiceMvc.start();
    } catch (Exception e) {
      log.error("수집 중 에러 :{}", e);
    }
    try {
      log.info("=================네이버 수집 시작됨===================");
      naverServiceMvc.start();
    } catch (Exception e) {
      log.error("수집 중 에러 :{}", e);
    }
    try {
      log.info("=================번개장터 수집 시작됨===================");
      bunJangServiceMvc.start();
    } catch (Exception e) {
      log.error("수집 중 에러 :{}", e);
    }
    executorService.shutdown();
  }

  private void collectCarrotAndJunggoByTread(ExecutorService executorService) {
    executorService.submit(
        () -> {
          Integer productCount = 0;
          try {
            log.info("=================당근마켓 수집 시작됨===================");
            productCount = carrotServiceMvc.start();
          } catch (Exception e) {
            log.error("당근마켓 수집 에러 ={}", e);
          }

          if (productCount != 0) {
            log.info("=================중고나라 수집 시작됨===================");
            completionHandler.completed(null, junggonaraServiceMvc);
          }
        });
  }

  private static final CompletionHandler<Object, JunggonaraServiceMvc> completionHandler = new CompletionHandler<Object, JunggonaraServiceMvc>() {
    @Override
    public void completed(Object result, JunggonaraServiceMvc junggonaraServiceMvc) {
      junggonaraServiceMvc.start();
    }

    @Override
    public void failed(Throwable exc, JunggonaraServiceMvc attachment) {
      log.error("중고나라 수집이 실패하였습니다={}", exc.toString());
    }
  };
}

