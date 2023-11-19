package team.three.usedstroller.collector.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.three.usedstroller.collector.dto.FilterReq;
import team.three.usedstroller.collector.dto.ProductRes;

public interface CustomProductRepository {

  Page<ProductRes> getProducts(FilterReq filter, Pageable pageable);
}
