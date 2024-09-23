package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.entity.CompanyPhotos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyPhotosDao extends JpaRepository<CompanyPhotos, Integer> {


    //取得照片
    List<CompanyPhotos> findByCompanyId( Integer companyId);

}
