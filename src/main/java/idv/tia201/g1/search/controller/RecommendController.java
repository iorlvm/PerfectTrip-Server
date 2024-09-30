package idv.tia201.g1.search.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.search.dto.RecommendCompanyDTO;
import idv.tia201.g1.search.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    @GetMapping("hot")
    public Result getHotCompany(@RequestParam(defaultValue = "4") Integer size) {
        List<RecommendCompanyDTO> hotCompany = recommendService.getHotCompany(size);
        return Result.ok(hotCompany);
    }

    @GetMapping("rand")
    public Result getRandCompany(@RequestParam(defaultValue = "4") Integer size) {
        List<RecommendCompanyDTO> randCompany = recommendService.getRandCompany(size);
        return Result.ok(randCompany);
    }

    @GetMapping("discount")
    public Result getDiscountCompany(@RequestParam Date date, @RequestParam(defaultValue = "4") Integer size) {
        List<RecommendCompanyDTO> discountCompany = recommendService.getDiscountCompany(date, size);
        return Result.ok(discountCompany);
    }
}
