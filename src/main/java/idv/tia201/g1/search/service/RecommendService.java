package idv.tia201.g1.search.service;

import idv.tia201.g1.search.dto.RecommendCompanyDTO;

import java.sql.Date;
import java.util.List;

public interface RecommendService {
    List<RecommendCompanyDTO> getHotCompany(Integer size);

    List<RecommendCompanyDTO> getDiscountCompany(Date date, Integer size);

    List<RecommendCompanyDTO> getRandCompany(Integer size);
}