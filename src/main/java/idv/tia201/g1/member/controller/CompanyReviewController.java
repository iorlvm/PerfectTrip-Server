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
        Long total = reviews.getTotalElements();
        CompanyReview userReviews = companyReviewService.getUserReviews(companyId);

        if (userReviews != null) {
            total++;
        }

        return Result.ok(reviews.getContent(), total);
    }

    @GetMapping("{companyId}/user")
    public Result getUserReviews(@PathVariable Integer companyId) {
        CompanyReview userReviews = companyReviewService.getUserReviews(companyId);
        return Result.ok(userReviews);
    }

    @PostMapping("{companyId}")
    public Result addReviews(
            @PathVariable Integer companyId,
            @RequestBody CompanyReview companyReview
    ) {
        CompanyReview saved = companyReviewService.addReviews(companyId, companyReview);
        return Result.ok(saved);
    }

    @PutMapping("{companyReviewId}")
    public Result editReview(
            @PathVariable Integer companyReviewId,
            @RequestBody CompanyReview companyReview
    ) {
        CompanyReview editReviews = companyReviewService.editReviews(companyReviewId, companyReview);
        return Result.ok(editReviews);
    }

    @DeleteMapping("{companyReviewId}")
    public Result deleteReview(@PathVariable Integer companyReviewId) {
        companyReviewService.deleteById(companyReviewId);
        return Result.ok();
    }
}
