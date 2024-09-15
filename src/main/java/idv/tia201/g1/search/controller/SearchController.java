package idv.tia201.g1.search.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.search.dto.SearchRequest;
import idv.tia201.g1.search.dto.SearchResponse;
import idv.tia201.g1.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return Result.ok();
    }
}
