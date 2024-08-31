package idv.tia201.g1.product.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.stat.descriptive.summary.Product;

import java.io.Serializable;

@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails implements Serializable {

    @Id
    @Column(name = "product_id")
    private int productId;

    @Column(name = "includes_breakfast")
    private boolean includesBreakfast;

    @Column(name = "allow_date_changes")
    private boolean allowDateChanges;

    @Column(name = "is_refundable")
    private boolean isRefundable;

    @Column(name = "allow_free_cancellation")
    private boolean allowFreeCancellation;

//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "product_id")
//    private Product product;
}