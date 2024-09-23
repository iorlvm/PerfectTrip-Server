package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.ProductFacilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ProductFacilitiesDao extends JpaRepository<ProductFacilities, Integer> {
    @Modifying
    @Query("DELETE FROM ProductFacilities pf WHERE pf.productId = :productId AND pf.facilityId NOT IN :facilityIds")
    void deleteByProductIdAAndFacilityIdNotIn(@Param("productId") Integer productId, @Param("facilityIds") List<Integer> facilityIds);

    @Query("SELECT pf.facilityId FROM ProductFacilities pf WHERE pf.productId = :productId")
    List<Integer> findFacilityIdsByProductId(@Param("productId") Integer productId);
}
