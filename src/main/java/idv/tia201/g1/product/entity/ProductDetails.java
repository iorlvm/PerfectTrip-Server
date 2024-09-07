package idv.tia201.g1.product.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name = "product_details")
public class ProductDetails {
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

    @OneToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(
                    referencedColumnName = "product_id",
                    name = "product_id"),
            name = "product_facilities",
            inverseJoinColumns = @JoinColumn(
                    name = "facility_id",
                    referencedColumnName = "facility_id")
    )
    private List<Facility> facilities;
}