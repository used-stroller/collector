package team.three.usedstroller.collector.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -2005534805L;

    public static final QProduct product = new QProduct("product");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final StringPath address = createString("address");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath etc = createString("etc");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgSrc = createString("imgSrc");

    public final StringPath link = createString("link");

    public final StringPath pid = createString("pid");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath region = createString("region");

    public final NumberPath<Integer> releaseYear = createNumber("releaseYear", Integer.class);

    public final EnumPath<SourceType> sourceType = createEnum("sourceType", SourceType.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final DatePath<java.time.LocalDate> uploadDate = createDate("uploadDate", java.time.LocalDate.class);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

