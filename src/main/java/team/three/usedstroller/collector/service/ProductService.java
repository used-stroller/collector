package team.three.usedstroller.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.three.usedstroller.collector.dto.FilterReq;
import team.three.usedstroller.collector.dto.ProductRes;
import team.three.usedstroller.collector.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;

  public Page<ProductRes> getProducts(FilterReq filter, Pageable pageable) {
    return productRepository.getProducts(filter, pageable);
  }

}
