package idv.tia201.g1.product.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@Entity
@Table(name = "product_photos")
public class ProductPhotos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id", nullable = false)
    private Integer photoId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "photo_url", nullable = false, length = 255)
    private String photoUrl;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_main", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isMain;
}

