package team.three.usedstroller.collector.service.mvc;

import static team.three.usedstroller.collector.util.UnitConversionUtils.changeLocalDate;
import static team.three.usedstroller.collector.util.UnitConversionUtils.convertToTimeFormat;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import team.three.usedstroller.collector.domain.entity.Model;
import team.three.usedstroller.collector.domain.entity.Product;
import team.three.usedstroller.collector.domain.dto.FilterReq;
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

    //1. 모델리스트 가져오기
    List<Model> allModels = modelRepository.findAll();

    //2. 모델명 반복하면서 상품과 맵핑
    for (int i = 0; i < allModels.size(); i++) {

      //2-1 브랜드와 모델명 filter에 set
      List<String> brand = new ArrayList<>();
      brand.add(allModels.get(i).getBrand());
      FilterReq filter = new FilterReq(allModels.get(i).getName(), null, null, null, null, null,
          null, brand);

      //2-2 상품 리스트 출력 by filter
      List<Product> filteredProduct = productRepository.getProductsOnly(filter);

      //2-3 모델객체 검색
      Model obj = modelRepository.findByName(filter.getKeyword());

      //2-4 상품리스트에 모델 객체 set
      updateModelIdColumnProductTable(filteredProduct, obj);
    }
  }

  private void updateModelIdColumnProductTable(List<Product> filteredProduct, Model obj) {
    for (Product product : filteredProduct) {
      product.setModel(obj);
      productRepository.save(product);
    }
  }

  public void updateNullDate() {
    List<Product> list = productRepository.getNullDateList();
    for (Product product : list) {
      try {
        LocalDate uploadDate = getUploadDate(product);
        product.setUploadDate(uploadDate);
        productRepository.save(product);
      } catch (IOException e) {
        // 페이지 요청 실패 시 건너뜀
        log.error("당근마켓 상세정보 가져오기 실패 URL: {}", product.getLink(), e);
      }
    }


  }

  private static LocalDate getUploadDate(Product product) throws IOException {
    String uploadTime;
    Document detailDoc;
    detailDoc = Jsoup.connect(product.getLink()).get();
    Element time = detailDoc.getElementsByTag("time").stream().findFirst()
        .orElseGet(() -> null);
    uploadTime = ObjectUtils.isEmpty(time) ? "" : time.text().replace("끌올", "").replace("\\D", "");
    LocalDate uploadDate = changeLocalDate(convertToTimeFormat(uploadTime));
    return uploadDate;
  }
}

