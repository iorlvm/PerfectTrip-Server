package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.entity.CompanyPhotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyPhotosDao extends JpaRepository<CompanyPhotos, Integer> {


    //取得照片
    List<CompanyPhotos> findByCompanyId( Integer companyId);

    @Query(value = "SELECT cp.photo_url FROM company_photos cp WHERE cp.company_id = :companyId ORDER BY cp.is_main DESC LIMIT 1", nativeQuery = true)
    String findMainPhotoByCompanyId(@Param("companyId") Integer companyId);
}
