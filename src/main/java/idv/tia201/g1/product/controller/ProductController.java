package idv.tia201.g1.product.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.product.dao.FacilityDao;
import idv.tia201.g1.product.dto.AddProductRequest;
import idv.tia201.g1.product.dto.ProductResponse;
import idv.tia201.g1.product.dto.RoomResponse;
import idv.tia201.g1.product.dto.UpdateProductRequest; // 新增导入
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/product")
@RestController
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private FacilityDao facilityDao;

    @GetMapping("facility")
    public Result getFacility() {
        return Result.ok(facilityDao.findAll());
    }

    @PostMapping("/add")
    public Result handleAddProduct(@RequestBody AddProductRequest request) {
        try {
            Product savedProduct = productService.handleAddProduct(request);
            ProductResponse productResponse = new ProductResponse(
                    savedProduct.getProductId(),
                    savedProduct.getProductName(),
                    savedProduct.getPrice(),
                    savedProduct.getMaxOccupancy(),
                    savedProduct.getStock(),
                    savedProduct.getCompanyId()
            );
            return Result.ok(productResponse);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/allTypes")
    public Result getProductTypes() {
        try {
            List<Product> productTypes = productService.getAllProductTypes();

            if (productTypes == null || productTypes.isEmpty()) {
                return Result.fail("沒有搜尋到可用的產品");
            }
            return Result.ok(productTypes, (long) productTypes.size());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/delete/room/{roomId}")
    public Result deleteRoom(@PathVariable Long roomId) {
        try {
            productService.deleteProduct(roomId);
            return Result.ok();
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/update/{roomId}")
    public Result updateRoom(
            @PathVariable Long roomId,
            @RequestBody UpdateProductRequest updateProductRequest) {

        try {

            System.out.println("Product ID: " + roomId);
            System.out.println("Product Name: " + updateProductRequest.getProductName());
            System.out.println("Room Price: " + updateProductRequest.getRoomPrice());
            System.out.println("Stock: " + updateProductRequest.getStock());


            Product updatedProduct = productService.updateProduct(
                    roomId,
                    updateProductRequest.getProductName(),
                    updateProductRequest.getRoomPrice(),
                    null,  // photoBytes
                    null,  // 其他参数
                    updateProductRequest.getStock()
            );

            RoomResponse roomResponse = new RoomResponse(
                    updatedProduct.getProductId(),
                    updatedProduct.getProductName(),
                    updatedProduct.getPrice()
            );

            return Result.ok(roomResponse);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/all")
    public Result getAllProducts() {
        Result result = productService.getAllProducts();

        if (result == null || !result.getSuccess()) {
            return Result.fail(result == null ? "取得產品失敗" : result.getErrorMsg());
        }

        Object data = result.getData();
        if (data == null ) {
            return Result.fail("沒有搜尋到可用產品");
        }

        List<Product> products = (List<Product>) data;

        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            ProductResponse productResponse = new ProductResponse(
                    product.getProductId(),
                    product.getProductName(),
                    product.getPrice(),
                    product.getMaxOccupancy(),
                    product.getStock(),
                    product.getCompanyId()
            );
            productResponses.add(productResponse);
        }

        return Result.ok(productResponses, (long) productResponses.size());
    }
}
