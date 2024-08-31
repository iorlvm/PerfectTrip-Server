package idv.tia201.g1.product.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_facilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFacilities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "facility_id")
    private int facilityId;

//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    @ManyToOne
//    @JoinColumn(name = "facility_id")
//    private Facility facility;
}
