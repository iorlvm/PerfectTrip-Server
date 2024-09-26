package idv.tia201.g1.product.dao;

import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.product.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FacilityDao extends JpaRepository<Facility, Integer> {
    @Query("FROM Facility f JOIN  CompanyFacility cf " +
            "ON cf.facilityId = f.facilityId " +
            "WHERE :companyId = cf.companyId ")
    List<Facility> findByCompanyId(@Param("companyId") Integer companyId);
}
