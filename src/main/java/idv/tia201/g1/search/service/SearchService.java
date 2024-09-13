package idv.tia201.g1.search.service;

import idv.tia201.g1.search.dto.SearchRequest;
import idv.tia201.g1.search.dto.SearchResponse;
import org.springframework.data.domain.Page;

public interface SearchService {
    Page<SearchResponse> search(SearchRequest searchRequest);
}
