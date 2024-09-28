package idv.tia201.g1.member.service;

import idv.tia201.g1.member.dto.CompanyReviewDTO;
import idv.tia201.g1.member.entity.CompanyReview;
import org.springframework.data.domain.Page;

public interface CompanyReviewService {
    Page<CompanyReviewDTO> getReviews(Integer companyId, Integer page, Integer size);

    CompanyReview addReviews(Integer companyId, CompanyReview companyReview);

    void deleteById(Integer companyReviewId);

    CompanyReview editReviews(Integer companyReviewId, CompanyReview companyReview);
}