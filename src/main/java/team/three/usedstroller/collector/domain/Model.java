package team.three.usedstroller.collector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "model")
public class Model extends BaseTimeEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String brand;
  private String price;

//  @OneToMany(mappedBy = "model")
//  private List<Product> products = new ArrayList<>();
//
//  public void addProduct(Product product) {
//    this.products.add(product);
//    // 무한루프 방지
//    if (product.getModel() != this) {
//      product.setModel(this);
//    }
//  }

  @Builder
  private Model(Long id, String name, String brand, String price) {
    this.name = name;
    this.brand = brand;
    this.price = price;
  }
}
