package idv.tia201.g1.search.service;

import idv.tia201.g1.search.dto.SearchProductResponse;
import idv.tia201.g1.search.dto.SearchRequest;
import idv.tia201.g1.search.dto.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SearchService {
    @Transactional(readOnly = true)
    Page<SearchResponse> search(SearchRequest searchRequest);

    @Transactional(readOnly = true)
    List<SearchProductResponse> searchProductListByCompanyId(Integer companyId, SearchRequest searchRequest);

    @Transactional
    void deleteSearchCache(SearchRequest searchRequest);
}