package idv.tia201.g1.search.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.search.dto.SearchProductResponse;
import idv.tia201.g1.search.dto.SearchRequest;
import idv.tia201.g1.search.dto.SearchResponse;
import idv.tia201.g1.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping
    public Result search(SearchRequest searchRequest) {
        Page<SearchResponse> search = searchService.search(searchRequest);
        return Result.ok(search.getContent(), search.getTotalElements());
    }

    @GetMapping("{companyId}")
    public Result getProductList(@PathVariable Integer companyId, SearchRequest searchRequest) {
        List<SearchProductResponse> productResponses = searchService.searchProductListByCompanyId(companyId, searchRequest);
        return Result.ok(productResponses);
    }

    @DeleteMapping("delete-cache")
    public Result deleteCache(@RequestBody SearchRequest searchRequest) {
        searchService.deleteSearchCache(searchRequest);
        return Result.ok();
    }
}
