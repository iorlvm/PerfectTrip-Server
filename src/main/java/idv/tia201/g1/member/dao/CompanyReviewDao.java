package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.dto.CompanyReviewDTO;
import idv.tia201.g1.member.entity.CompanyReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyReviewDao extends JpaRepository<CompanyReview, Integer> {

    @Query("SELECT new idv.tia201.g1.member.dto.CompanyReviewDTO(u.nickname, cr.starRank, cr.comment) " +
            "FROM CompanyReview cr " +
            "JOIN User u ON u.userId = cr.userId " +
            "WHERE cr.companyId = :companyId AND u.userId <> :userId")
    Page<CompanyReviewDTO> findByCompanyId(
            @Param("companyId") Integer companyId,
            @Param("userId") Integer userId,
            Pageable pageable);

    CompanyReview findByCompanyIdAndUserId(Integer companyId, Integer userId);

    Integer countByCompanyId(Integer companyId);

    @Query("SELECT AVG(cr.starRank) FROM CompanyReview cr WHERE cr.companyId = :companyId")
    Float avgStarRankByCompanyId(@Param("companyId") Integer companyId);
}
