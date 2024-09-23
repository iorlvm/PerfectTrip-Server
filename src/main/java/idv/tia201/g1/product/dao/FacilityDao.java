package idv.tia201.g1.product.dao;

import idv.tia201.g1.product.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FacilityDao extends JpaRepository<Facility, Integer> {
}
