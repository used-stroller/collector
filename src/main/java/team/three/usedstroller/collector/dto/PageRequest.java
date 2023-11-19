package team.three.usedstroller.collector.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

@Getter
public final class PageRequest {

  private final int page;
  private final int size;
  private final String sort;

  public PageRequest(Integer page, Integer size, String sort) {
    int DEFAULT_SIZE = 10;
    int MAX_SIZE = 100;

    if (size == null) {
      this.size = DEFAULT_SIZE;
      this.page = page == null ? 0 : page;
    } else if (page == null) {
      this.size = size;
      this.page = 0;
    } else {
      this.size = size > MAX_SIZE || size < 0 ? DEFAULT_SIZE : size;
      this.page = page < 0 ? 0 : page;
    }

    this.sort = sort;
  }

  public Pageable of() {

    if (this.sort == null && this.size == 0) {
      return UnPaged.of();
    }

    Sort sortFromString = getSortFromString(this.sort);

    return org.springframework.data.domain.PageRequest.of(page, size, sortFromString);
  }

  private Sort getSortFromString(String sort) {

    if (sort == null) {
      return Sort.unsorted();
    }

    String[] split = sort.split(",");
    if (split.length >= 2) {
      List<Order> orders = new ArrayList<>();
      for (int i = 0; i < split.length; i = i+2) {
        orders.add(new Order(Direction.fromString(split[i+1].trim()), split[i]));
      }

      return Sort.by(orders);
    }

    return Sort.unsorted();
  }


}
