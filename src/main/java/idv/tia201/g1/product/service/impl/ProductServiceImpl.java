package idv.tia201.g1.product.service.impl;

import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.image.entity.Image;
import idv.tia201.g1.image.service.ImageService;
import idv.tia201.g1.product.dao.*;
import idv.tia201.g1.product.dto.AddProductRequest;
import idv.tia201.g1.product.entity.ProductDetails;
import idv.tia201.g1.product.entity.ProductFacilities;
import idv.tia201.g1.product.entity.ProductPhotos;
import idv.tia201.g1.product.exception.InternalServerException;
import idv.tia201.g1.product.exception.ResourceNotFoundException;
import idv.tia201.g1.product.service.ProductService;
import idv.tia201.g1.product.entity.Product;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static idv.tia201.g1.core.utils.Constants.ROLE_ADMIN;
import static idv.tia201.g1.core.utils.Constants.ROLE_COMPANY;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;
    @Autowired
    ProductDetailsDao productDetailsDao;
    @Autowired
    ProductFacilitiesDao productFacilitiesDao;
    @Autowired
    ProductPhotosDao productPhotosDao;
    @Autowired
    ImageService imageService;
    @Autowired
    Product product;


    /*** 新增 Product 方法 ***/
    @Override
    public Product handleAddProduct(AddProductRequest addProductRequest) {
        // 請求參數檢查
        if (addProductRequest == null) {
            throw new IllegalArgumentException("參數異常：請求對象為 null");
        }

        if (StringUtils.isBlank(addProductRequest.getProductName())) {
            throw new IllegalArgumentException("參數異常：商品名稱未填寫");
        }

        if (addProductRequest.getPrice() == null) {
            throw new IllegalArgumentException("參數異常：價格未填寫");
        }

        if (addProductRequest.getStock() == null) {
            throw new IllegalArgumentException("參數異常：庫存未填寫");
        }

        if (addProductRequest.getMaxOccupancy() == null) {
            throw new IllegalArgumentException("參數異常：最大入住人數未填寫");
        }

        // changeId 跟 companyId 應該要從登入中的使用者取得 (也就是從UserHolder工具中取出)
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：使用者未登入或身份不屬於商家");
        }

        // 將商品存入資料庫
        Product newProduct = new Product();
        BeanUtils.copyProperties(addProductRequest, newProduct);
        newProduct.setCompanyId(Long.valueOf(loginUser.getId()));
        newProduct.setChangeId(Long.valueOf(loginUser.getId()));
        Product saved = productDao.save(newProduct);

        // 取得剛剛存入的商品id
        Integer productId = saved.getProductId();

        // 取得請求中的細節設定id後, 存入資料庫
        ProductDetails productDetails = addProductRequest.getProductDetails();
        if (productDetails != null) {
            // 確保物件不為空
            productDetails.setProductId(productId);
            productDetailsDao.save(productDetails);
        }

        // 取得請求中所有的設施設定id後, 存入資料庫
        List<ProductFacilities> productFacilities = addProductRequest.getProductFacilities();
        if (productFacilities != null && !productFacilities.isEmpty()) {
            // 確保物件不為空
            productFacilities.forEach(facility -> facility.setProductId(productId));
            productFacilitiesDao.saveAll(productFacilities);
        }


        ImageUploadRequest imageUploadRequest = addProductRequest.getImageUploadRequest();
        if (imageUploadRequest != null) {
            // 有傳輸圖片的情況, 使用ImageService處理並存放圖片
            Image image = imageService.upload(imageUploadRequest);
            Image save = imageService.save(image);
            String url = "image/" + save.getId();

            ProductPhotos productPhotos = new ProductPhotos();
            productPhotos.setProductId(productId);
            productPhotos.setPhotoUrl(url);
            productPhotos.setDescription(save.getComment());
            productPhotos.setMain(true);

            // 將圖片存放到相簿中
            productPhotosDao.save(productPhotos);
        }

        return newProduct;
    }


    @Override
    public Product updateProduct(Long productId,
                                 String productName,
                                 int roomPrice,
                                 byte[] photoByte,
                                 Integer maxOccupancy,
                                 int stock) {

        // 參數檢查，確保 productId 有效，否則丟出一 (IllegalArgumentException)
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("參數異常：產品ID無效");
        }

        // 權限檢查，檢查用戶是否有權限更新產品信息
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：使用者未登入或身份不屬於商家");
        }

        // 查詢產品是否存在，Optional方法如果其中有值，則返回該值
        // 如果沒有值，則根據傳入的 lambda 表達式拋出一個異常 (ResourceNotFoundException)
        Product product = productDao
                .findById(Math.toIntExact(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // 更新產品名稱 (ProductName)，確保內容不為 null 且有效
        // 用trim() 方法會去除 productName 字符串兩端的空白字符、制表字符
        if (productName != null && !productName.trim().isEmpty()) {
            product.setProductName(productName);
        }

        // 更新房間價格 (ProductPrice) 確保有效且不小於 0
        // 否則拋出一個異常 (IllegalArgumentException)
        if (roomPrice > 0) {
            product.setRoomPrice(BigDecimal.valueOf(roomPrice));
        } else if (roomPrice < 0 ) {
            throw new IllegalArgumentException("參數異常：房間價格無效");
        }  else {
            product.setRoomPrice(BigDecimal.valueOf(roomPrice));
        }

        // 更新最大入住人數 (maxOccupancy) 確保 maxOccupancy 不為 null 且不小於 0
        // 否則拋出一個異常 (IllegalArgumentException)
        if (maxOccupancy != null && maxOccupancy > 0) {
            product.setMaxOccupancy(maxOccupancy);
        } else if (maxOccupancy < 0) {
            throw new IllegalArgumentException("參數異常：入住人數無效");
        }

        // 更新庫存 (stock) 確保 stock 有效且不小於 0
        // 否則拋出一個異常 (IllegalArgumentException)
        if (stock >= 0) {
            product.setStock(stock);
        } else if (stock < 0) {
            throw new IllegalArgumentException("參數異常：庫存數量無效");
        }

        // 處理照片 (photo) 更新，檢查照片的字節數組是否有內容。如果字節數組的長度為 0
        // 則表示沒有有效的照片數據（空數據)，BLOB（Binary Large Object）
        // setPhoto 將該數據轉換為一個 SerialBlob 對象
        if (photoByte != null && photoByte.length > 0) {
            try {
                product.setPhoto(new SerialBlob(photoByte));
            } catch (SQLException e) {
                throw new InternalServerException("Error updating photo", e);
            }
        }

        productDao.save(product);
        return product;
    }




    /**
     * 查看全部的商品型態
     **/
    @Override
    public List<Product> getAllProductTypes() {

        // 權限檢查，如果當前用戶沒有登錄或登錄的用戶角色不是管理員
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_ADMIN.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：未登入或無查看所有產品類型的權限");
        }
        // 查詢數據庫獲取所有產品信息
        List<Product> productTypes = productDao.findAll();

        // 確保結果不為空，如果為空，返回空列表
        if (productTypes == null || productTypes.isEmpty()) {
            return new ArrayList<>();
        }

        return productTypes;
    }

    /** 尋找資料 ById  **/
    // Optional 可以解決可能出現的空指針異常（NullPointerException）問題，Optional<T>
    // 作為泛型表示一個可能包含或不包含非 null 值的對象。還可以結合返回特定值 orElse()

    @Override
    public Optional<Product> getProductById(Long productId) {

        // 參數檢查：確保 productId 不為空或無效
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("參數異常：產品ID無效");
        }

        // 權限檢查
        UserAuth loginUser = UserHolder.getUser();
        if ( loginUser == null || !ROLE_ADMIN.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：未登入或無查看所有產品類型的權限");
        }

        // 從資料庫中根據 productId 查詢產品
        Optional<Product> product = productDao.findById(Math.toIntExact(productId));

        // 直接返回查詢結果
        return product;
    }

    /** 查看所有商品 **/
    @Override
    public List<Product> getAllProducts() {

        // 權限檢查：確保用戶具有查看所有產品的權限
        // 如果當前用戶沒有登錄或登錄的用戶角色不是管理員
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_ADMIN.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：未登入或無權限查看所有產品");
        }

        // 查詢數據庫獲取所有產品列表
        List<Product> products = productDao.findAll();

        // 確認查詢結果不為空：如果結果為空，返回空列表
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }

        // 返回查詢結果
        return products;
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Integer id) {
        return new byte[0];
    }


    @Override
    public void deleteProduct(Long productId) {

        // 參數檢查：確保 productId 不為 null 且有效
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("參數異常：產品ID無效");
        }

        // 權限檢查：確保用戶具有刪除產品的權限
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_ADMIN.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：未登入或無刪除產品的權限");
        }

        // 確認要刪除的產品是否存在
        Optional<Product> theProduct = productDao.findById(Math.toIntExact(productId));
        // 不存在就回傳一個失敗值
        if (!theProduct.isPresent()) {
            throw new IllegalStateException("操作失敗：產品不存在");
        }

        // 執行刪除操作
        productDao.deleteById(Math.toIntExact(productId));
    }


}
