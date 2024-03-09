package team.three.usedstroller.collector.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.domain.SourceType;
import team.three.usedstroller.collector.repository.ProductRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventHandler {

  private final ProductRepository productRepository;

  @Async
  @EventListener
  @Transactional
  public void deleteProductBySourceType(SourceType sourceType) {
    log.info("이벤트 구독 {}", sourceType);
    LocalDateTime today = LocalDateTime.now().minusHours(3);
    productRepository.deleteAllBySourceTypeAndUpdatedAtIsBefore(sourceType, today);
    log.info("[{}] 과거 데이터 삭제 완료. 기준 일시: [{}]", sourceType, today);
  }

}
