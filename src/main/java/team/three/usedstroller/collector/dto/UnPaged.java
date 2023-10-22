package team.three.usedstroller.collector.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnPaged implements Pageable {

  private Sort sort;

  public UnPaged(Sort sort) {
    this.sort = sort;
  }

  public static Pageable of(Sort sort) {

    return new UnPaged(sort);
  }

  public static Pageable of() {
    return new UnPaged(null);
  }

  @Override
  public int getPageNumber() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getPageSize() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getOffset() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Sort getSort() {

    if (this.sort == null) {
      return Sort.unsorted();
    }
    return this.sort;
  }

  @Override
  public Pageable next() {
    return this;
  }

  @Override
  public Pageable previousOrFirst() {
    return this;
  }

  @Override
  public Pageable first() {
    return this;
  }

  @Override
  public Pageable withPage(int pageNumber) {
    return this;
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }
}
