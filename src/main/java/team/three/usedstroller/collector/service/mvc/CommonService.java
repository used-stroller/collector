package team.three.usedstroller.collector.service.mvc;

import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.three.usedstroller.collector.domain.Model;
import team.three.usedstroller.collector.repository.ModelRepository;
import team.three.usedstroller.collector.repository.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

  private final BunJangServiceMvc bunJangServiceMvc;
  private final CarrotServiceMvc carrotServiceMvc;
  private final JunggonaraServiceMvc junggonaraServiceMvc;
  private final SecondWearServiceMvc secondWearServiceMvc;
  private final NaverServiceMvc naverServiceMvc;
  private final ProductRepository productRepository;
  private final ModelRepository modelRepository;

  public void collectAll() {
    ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Thread1 중고나라,번개장터
     */
    collectByThread(executorService);

    /**
     * Thread2 당근 및 기타
     */
    try {
      log.info("=================당근마켓 수집 시작됨===================");
      carrotServiceMvc.start();
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
      log.info("=================세컨웨어 수집 시작됨===================");
      secondWearServiceMvc.start();
    } catch (Exception e) {
      log.error("수집 중 에러 :{}", e);
    }
    executorService.shutdown();
  }

  private void collectByThread(ExecutorService executorService) {
    executorService.submit(
        () -> {
          try {
            log.info("=================중고나라 수집 시작됨===================");
            junggonaraServiceMvc.start();
          } catch (Exception e) {
            log.error("중고나라 수집 에러 ={}", e);
          }

          log.info("=================번개장터 수집 시작됨===================");
          completionHandler.completed(null, bunJangServiceMvc);
        });
  }

  private static final CompletionHandler<Object, BunJangServiceMvc> completionHandler = new CompletionHandler<Object, BunJangServiceMvc>() {
    @Override
    public void completed(Object result, BunJangServiceMvc bunJangServiceMvc) {
      bunJangServiceMvc.start();
    }

    @Override
    public void failed(Throwable exc, BunJangServiceMvc attachment) {
      log.error("번개장터 수집이 실패하였습니다={}", exc.toString());
    }
  };

  public void updateModel() {
    //브랜드와 모델명 가져오기
    List<Model> allModels = modelRepository.findAll();
    //모델리스트 가져오기
    //List<ProductRes> productsOnly = productRepository.getProductsOnly();

    //모델키워드 넣어서 조회하고 조회한 값들에 대해서 모델명 넣기
  }
}

