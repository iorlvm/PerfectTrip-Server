package idv.tia201.g1.product.controller;

import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.product.dto.AddProductRequest;
import idv.tia201.g1.product.dto.ProductResponse;
import idv.tia201.g1.product.dto.RoomResponse;
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* 使用Lombok可以減少樣板編碼，包含 Getter, Setter, 構造函數, toString(), equals(), hashCode()等方法
 *  此註釋自動生成包含 final 字段或是標註為 @NonNull 的構造函數，對於一個類有多個依賴項可以減少手動編寫構造函數
 * Spring 的 @Autowired */
@RequiredArgsConstructor

/* Spring MVC 的註解，定義 HTTP 請求的 URL映射，表示這個類的所有方法將對應 /product 路徑
 * 的 HTTP 請求，包含 GET, POST, PUT, DELETE */
@RequestMapping("/product")

/* Spring MVC 的註解，為 @Controller 與 @ResponseBody 的結合體，
 * 標記一個類為 RESTful 網絡服務的控制器，並且它的所有方法返回的結果，
 * 都會直接寫入 HTTP 響應體中，而不是解析為一個視圖 View */
@RestController
public class ProductController {

    @Autowired
    // 產品相關業務邏輯
    private final ProductService productService;

    /*
     允許來自特定來源的跨域請求，跨域資源共享 CORS 允許不同域名之間的請求方法
     來自前端的 HTTP 能訪問此控制器
     */
    @CrossOrigin(origins = "http://localhost:5173")

    /*
     * 處理 HTTP POST 請求的方法，調用來向服務器發送新產品添加請求的操作
     */
    @PostMapping("/add")
    // 返回帶有 ProductResponse 資料的 HTTP 回應實體
    public ResponseEntity<Object> handleAddProduct(
            @RequestParam("companyId") Long companyId,
            @RequestParam("changeId") Long changeId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("productName") String productName,
            @RequestParam("roomPrice") int roomPrice,
            @RequestParam("stock") int stock,
            @RequestParam("maxOccupancy") int maxOccupancy) throws SQLException, IOException {

        // 構建 AddProductRequest 對象，包含從請求中提取的所有參數
        AddProductRequest request = new AddProductRequest();
        request.setProductName(productName);
        request.setPrice(roomPrice);
        request.setStock(stock);
        request.setMaxOccupancy(maxOccupancy);

        // 圖片處理
        if (photo != null && !photo.isEmpty()) {
            request.setImageUploadRequest(new ImageUploadRequest(photo.getBytes(), photo.getOriginalFilename()));
        }

        // 調用 productService 的 handleAddProduct 方法
        Product savedProduct = productService.handleAddProduct(request);

        // 創建 ProductResponse 對象，封裝響應數據
        ProductResponse productResponse = new ProductResponse(
                savedProduct.getId(),
                savedProduct.getProductName(),
                savedProduct.getRoomPrice()
        );

        // 返回 HTTP 200 OK 的回應，並包含 productResponse 對象
        return ResponseEntity.ok(productResponse);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    // 標示了一個處理 HTTP GET 請求的方法，並且這個請求的路徑必須匹配 /allTypes
    @GetMapping("/allTypes")
    public ResponseEntity<?> getProductTypes() throws SQLException {
        List<Product> productTypes = productService.getAllProductTypes();

        if (productTypes == null || productTypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No product types available.");
        }
        return ResponseEntity.ok(productTypes);
    }

    // 處理 HTTP GET 請求，返回所有產品
    @GetMapping("/all")
    public ResponseEntity<List<RoomResponse>> getAllProducts() throws SQLException {
        List<Product> products = productService.getAllProducts();

        if (products == null || products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }

        // 將產品轉換為 RoomResponse 並返回
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Product product : products) {
            RoomResponse roomResponse = getRoomResponse(product);
            roomResponses.add(roomResponse);
        }

        return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomResponse(Product product) {
        return new RoomResponse(product.getId(), product.getProductName(), product.getRoomPrice());
    }

    // 處理 HTTP DELETE 請求，刪除指定的房間
    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        try {
            productService.deleteProduct(roomId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 處理 HTTP PUT 請求，更新指定的房間
    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam String productName,
                                                   @RequestParam int roomPrice,
                                                   @RequestParam MultipartFile photo)
            throws IOException, SQLException {

        byte[] photoBytes = (photo != null && !photo.isEmpty()) ? photo.getBytes() : productService.getRoomPhotoByRoomId(Math.toIntExact(roomId));

        Product updatedProduct = productService.updateProduct(roomId, productName, roomPrice, photoBytes, null, 0);
        RoomResponse roomResponse = new RoomResponse(updatedProduct.getId(), updatedProduct.getProductName(), updatedProduct.getRoomPrice());

        return ResponseEntity.ok(roomResponse);
    }
}
