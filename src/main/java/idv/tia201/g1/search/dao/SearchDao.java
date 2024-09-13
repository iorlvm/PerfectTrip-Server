package idv.tia201.g1.search.dao;


import idv.tia201.g1.search.dto.ProductCalculation;
import idv.tia201.g1.search.dto.SearchResponse;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface SearchDao {
    List<Integer> findCompanyIdsByCityOrCountry(String destination);

    Map<Integer, List<ProductCalculation>> getProductCalculations(List<Integer> companyIds, Date startDate, Date endDate);

    SearchResponse getDetailsByProductIds(List<Integer> productIds);
}
