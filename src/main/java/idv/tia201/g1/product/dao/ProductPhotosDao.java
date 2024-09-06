package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.ProductPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotosDao extends JpaRepository<ProductPhotos, Integer> {
}
