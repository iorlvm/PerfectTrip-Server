package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.entity.CompanyFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyFacilityDao extends JpaRepository<CompanyFacility, Integer> {


//        @Query("SELECT cf FROM CompanyFacility cf " +
//                "JOIN Facility f ON cf.facilityId = f.facilityId " +
//                "WHERE cf.companyFacilityId = :companyFacilityId")
//        CompanyFacility findCompanyFacilityById(@Param("companyFacilityId") Integer companyFacilityId);

        //設施  TODO將所有的商店與設施關聯刪掉 DELET FACILITY BY COMPANY_ID  新增FACILITTY LIST
        @Modifying
        @Query("DELETE FROM CompanyFacility cf WHERE cf.companyId = :companyId")
        void deleteByCompanyId(@Param("companyId") Integer companyId);

        //取得設施
        List<CompanyFacility> findByCompanyId(Integer companyId);

}
