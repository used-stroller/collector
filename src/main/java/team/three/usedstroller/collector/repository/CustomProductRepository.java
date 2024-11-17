package team.three.usedstroller.collector.repository;


import java.util.List;
import team.three.usedstroller.collector.domain.entity.Product;
import team.three.usedstroller.collector.domain.dto.FilterReq;

public interface CustomProductRepository {

  List<Product> getProductsOnly(FilterReq filter);

  List<Product> getNullDateList();
}
