package idv.tia201.g1.product.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.dto.AddDiscountRequest;
import idv.tia201.g1.product.entity.ProductDiscount;
import idv.tia201.g1.product.service.ProductDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("api/discount")
public class ProductDiscountController {

    @Autowired
    ProductDiscountService productDiscountService;

    // 測試成功
    // 添加新的優惠
    @PostMapping("/add")
    public Result addDiscount(@RequestBody AddDiscountRequest addDiscountRequest) {
        // 確保 startDate 和 endDate 不為 null
        if (addDiscountRequest.getStartDate() == null || addDiscountRequest.getEndDate() == null) {
            throw new IllegalArgumentException("開始日期和結束日期不能為空！");
        }

        ProductDiscount productDiscount = new ProductDiscount();
        productDiscount.setDiscountTitle(addDiscountRequest.getDiscountTitle());
        productDiscount.setDiscountRate(Float.valueOf(addDiscountRequest.getDiscountRate())/100);
        productDiscount.setStartDateTime(Timestamp.valueOf(addDiscountRequest.getStartDate().atStartOfDay()));
        productDiscount.setEndDateTime(Timestamp.valueOf(addDiscountRequest.getEndDate().atStartOfDay()));

        ProductDiscount savedDiscount = productDiscountService.addProductDiscount(productDiscount);
        return Result.ok(savedDiscount);
    }

    // 測試成功
    // 根據公司ID (company_id) 來查詢優惠
    @GetMapping("/company/{companyId}")
    public Result getByCompanyId(@PathVariable Integer companyId) {
        List<ProductDiscount> discounts = productDiscountService.getByCompanyId(companyId);

        if (discounts == null || discounts.isEmpty()) {
            return Result.fail("Error");
        } else {
            return Result.ok(discounts);
        }
    }

    @PutMapping("/update/{discountId}")
    public Result updateDiscount(@PathVariable Integer discountId, @RequestBody ProductDiscount productDiscount) {
        ProductDiscount updated = productDiscountService.updateDiscount(discountId, productDiscount);
        if (updated == null) {
            return Result.fail("Error");
        } else {
            return Result.ok(updated);
        }
    }

    @DeleteMapping("/delete/{discountId}")
    public void deleteDiscount(@PathVariable Integer discountId) {
        productDiscountService.deleteProductDiscount(discountId);
    }
}
