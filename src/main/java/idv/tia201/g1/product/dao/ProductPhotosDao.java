package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.ProductPhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPhotosDao extends JpaRepository<ProductPhotos, Integer> {
    @Modifying
    @Query("DELETE FROM ProductPhotos pp WHERE pp.productId = :productId AND pp.photoUrl NOT IN :photoUrls")
    void deleteByProductIdAndPhotoUrlNotIn(@Param("productId") Integer productId, @Param("photoUrls") List<String> photoUrls);

    @Query("SELECT pp.photoUrl FROM ProductPhotos pp WHERE pp.productId = :productId")
    List<String> findPhotoUrlsByProductId(@Param("productId") Integer productId);
}
