package idv.tia201.g1.product.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.dto.ProductRequest;
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.service.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/inventory")
@RequiredArgsConstructor
public class ProductInventoryController {

    @Autowired
    ProductInventoryService productInventoryService;

    @GetMapping("/all")
    public Result getAllInventory(){
        Result products = productInventoryService.getAllInventories();
        return Result.ok(products);
    }

    @PostMapping("/add")
    public Result addInventory(@RequestBody ProductRequest productRequest) {
        Product handleAddInventory = productInventoryService.addInventory(productRequest);
        return Result.ok(handleAddInventory);
    }

    @PostMapping("/update")
    public Result updateInventory(@RequestBody Product product){
        Product updatedProduct = productInventoryService.updateProduct(product);
        return Result.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteInventory(@PathVariable Integer id){
        productInventoryService.deleteProduct(id);
        return Result.ok("Delete success");
    }

    @GetMapping("/status")
    public Result getInventoryByStatus(@RequestParam String status) {
        // 獲取產品列表
        List<Product> products = productInventoryService.getInventoriesByStatus(status);
        // 將結果封裝到 Result 中，然後返回
        return Result.ok(products, (long) products.size());
    }

    // 根據日期篩選房型庫存
    @GetMapping("/filter-by-date")
    public Result getInventoryByDate(@RequestParam("startDate") String startDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = start.plusDays(1); // 結束日期為開始日期 + 1 天

        List<Product> products = productInventoryService.getInventoryByDateRange(start, end);
        return Result.ok(products);
    }







}
