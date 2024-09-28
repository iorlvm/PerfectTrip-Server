package idv.tia201.g1.member.service.Impl;

import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.member.dao.CompanyDao;
import idv.tia201.g1.member.dao.CompanyReviewDao;
import idv.tia201.g1.member.dao.UserDao;
import idv.tia201.g1.member.dto.CompanyReviewDTO;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.CompanyReview;
import idv.tia201.g1.member.entity.User;
import idv.tia201.g1.member.service.CompanyReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static idv.tia201.g1.core.utils.Constants.*;

@Service
public class CompanyReviewServiceImpl implements CompanyReviewService {
    @Autowired
    private CompanyReviewDao companyReviewDao;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private UserDao userDao;


    @Override
    public Page<CompanyReviewDTO> getReviews(Integer companyId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return companyReviewDao.findByCompanyId(companyId, pageRequest);
    }

    @Override
    public CompanyReview addReviews(Integer companyId, CompanyReview companyReview) {
        // 驗證 CompanyReview 的參數
        if (companyReview.getStarRank() == null || companyReview.getStarRank() < 0 || companyReview.getStarRank() > 5) {
            throw new IllegalArgumentException("評分必須在 1 到 5 之間");
        }
        if (companyReview.getComment() == null || companyReview.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("評論內容不可為空");
        }

        UserAuth loginUser = UserHolder.getUser();
        if (!ROLE_USER.equals(loginUser.getRole())) {
            throw new IllegalStateException("使用者未登入, 或是帳號權限不符合");
        }
        Company company = companyDao.findByCompanyId(companyId);
        if (company == null) {
            throw new IllegalArgumentException("該商家不存在");
        }

        companyReview.setCompanyId(companyId);
        companyReview.setUserId(loginUser.getId());
        companyReview.setChangeId(loginUser.getId());
        return companyReviewDao.save(companyReview);
    }

    @Override
    public void deleteById(Integer companyReviewId) {
        companyReviewDao.findById(companyReviewId)
                .ifPresent(companyReview -> companyReviewDao.delete(companyReview));
    }

    @Override
    public CompanyReview editReviews(Integer companyReviewId, CompanyReview companyReview) {
        CompanyReview reviewFromDb = companyReviewDao.findById(companyReviewId).orElse(null);

        if (reviewFromDb == null) {
            throw new IllegalArgumentException("該評論不存在");
        }

        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null) {
            throw new IllegalStateException("使用者未登入");
        }

        String role = loginUser.getRole();
        switch (role) {
            case ROLE_ADMIN:
                break;
            case ROLE_USER:
                if (reviewFromDb.getUserId().equals(loginUser.getId())) break;
            case ROLE_COMPANY:
            default:
                throw new IllegalStateException("不具有編輯權限");
        }

        boolean flag = false;
        String comment = companyReview.getComment();
        if (comment != null) {
            flag = true;
            reviewFromDb.setComment(comment);
        }

        Integer starRank = companyReview.getStarRank();
        if (starRank != null) {
            flag = true;
            reviewFromDb.setStarRank(starRank);
        }

        if (flag) {
            reviewFromDb.setChangeId(loginUser.getId());
        }
        return companyReviewDao.save(reviewFromDb);
    }
}
