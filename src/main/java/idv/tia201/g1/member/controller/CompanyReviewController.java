package idv.tia201.g1.member.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.member.dto.CompanyReviewDTO;
import idv.tia201.g1.member.entity.CompanyReview;
import idv.tia201.g1.member.service.CompanyReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class CompanyReviewController {
    @Autowired
    private CompanyReviewService companyReviewService;

    @GetMapping("{companyId}")
    public Result getReviews(
            @PathVariable Integer companyId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Page<CompanyReviewDTO> reviews = companyReviewService.getReviews(companyId, page, size);
        return Result.ok(reviews.getContent(), reviews.getTotalElements());
    }

    @PostMapping("{companyId}")
    public Result addReviews(
            @PathVariable Integer companyId,
            @RequestBody CompanyReview companyReview
    ) {
        CompanyReview saved = companyReviewService.addReviews(companyId, companyReview);
        return Result.ok(saved);
    }

    @DeleteMapping("{companyReviewId}")
    public Result deleteReview (@PathVariable Integer companyReviewId) {
        companyReviewService.deleteById(companyReviewId);
        return Result.ok();
    }
}
