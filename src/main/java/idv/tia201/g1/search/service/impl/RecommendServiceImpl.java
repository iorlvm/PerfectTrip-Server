package idv.tia201.g1.search.service.impl;

import idv.tia201.g1.member.dao.CompanyReviewDao;
import idv.tia201.g1.member.dto.CompanyEditDetailResponse;
import idv.tia201.g1.member.service.CompanyManagerService;
import idv.tia201.g1.search.dao.SearchDao;
import idv.tia201.g1.search.dto.RecommendCompanyDTO;
import idv.tia201.g1.search.service.RecommendService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    private SearchDao searchDao;
    @Autowired
    private CompanyReviewDao companyReviewDao;
    @Autowired
    private CompanyManagerService companyManagerService;

    private RecommendCompanyDTO getRecommendCompanyDTO (Integer companyId) {
        RecommendCompanyDTO recommendCompanyDTO = new RecommendCompanyDTO();
        CompanyEditDetailResponse companyDetail = companyManagerService.getCompanyDetail(companyId);
        BeanUtils.copyProperties(companyDetail.getCompany(), recommendCompanyDTO);
        recommendCompanyDTO.setPhotos(companyDetail.getPhotos());
        recommendCompanyDTO.setRateCount(companyReviewDao.countByCompanyId(companyId));
        return recommendCompanyDTO;
    }

    @Override
    public List<RecommendCompanyDTO> getHotCompany(Integer size) {
        List<Integer> companyIds = searchDao.getHotCompanyIds(size);

        List<RecommendCompanyDTO> recommendCompanyDTOS = new ArrayList<>(size);
        for (Integer companyId : companyIds) {
            RecommendCompanyDTO recommendCompanyDTO = getRecommendCompanyDTO(companyId);
            recommendCompanyDTOS.add(recommendCompanyDTO);
        }

        return recommendCompanyDTOS;
    }

    @Override
    public List<RecommendCompanyDTO> getDiscountCompany(Date date, Integer size) {
        List<Integer> companyIds = searchDao.getDiscountCompanyIds(date, size);

        List<RecommendCompanyDTO> recommendCompanyDTOS = new ArrayList<>(size);
        for (Integer companyId : companyIds) {
            RecommendCompanyDTO recommendCompanyDTO = getRecommendCompanyDTO(companyId);
            recommendCompanyDTOS.add(recommendCompanyDTO);
        }

        return recommendCompanyDTOS;
    }

    @Override
    public List<RecommendCompanyDTO> getRandCompany(Integer size) {
        List<Integer> companyIds = searchDao.getRandCompanyIds(size);

        List<RecommendCompanyDTO> recommendCompanyDTOS = new ArrayList<>(size);
        for (Integer companyId : companyIds) {
            RecommendCompanyDTO recommendCompanyDTO = getRecommendCompanyDTO(companyId);
            recommendCompanyDTOS.add(recommendCompanyDTO);
        }

        return recommendCompanyDTOS;
    }
}
