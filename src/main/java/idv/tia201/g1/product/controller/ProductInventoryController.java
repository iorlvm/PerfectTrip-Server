package idv.tia201.g1.product.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.dto.ProductRequest;
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.service.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<Product> products = (List<Product>)
                productInventoryService.getInventoriesByStatus(status);
        return Result.ok(products);
    }







}
