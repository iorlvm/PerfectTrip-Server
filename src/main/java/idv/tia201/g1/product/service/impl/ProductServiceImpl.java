package idv.tia201.g1.product.service.impl;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.image.entity.Image;
import idv.tia201.g1.image.service.ImageService;
import idv.tia201.g1.order.entity.Order;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static idv.tia201.g1.core.utils.Constants.*;

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


    @Override
    public Product updateProduct(Long productId,
                                 String productName,
                                 int roomPrice,
                                 byte[] photoByte,
                                 Integer maxOccupancy,
                                 int stock) {

        // 参数检查，确保 productId 有效
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("參數異常：產品ID無效");
        }

        // 权限检查，确保当前用户是商家角色
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：使用者未登入或身份不屬於商家");
        }

        // 查找产品，确保产品存在
        Product product = productDao
                .findById(Math.toIntExact(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // 更新产品名称
        if (productName != null && !productName.trim().isEmpty()) {
            product.setProductName(productName);
        }

        // 更新房间价格，确保价格为正数
        if (roomPrice > 0) {
            product.setPrice(roomPrice);
        } else {
            throw new IllegalArgumentException("參數異常：房間價格無效");
        }

        // 更新最大入住人数（如果存在并且有效）
        if (maxOccupancy != null) {
            if (maxOccupancy > 0) {
                product.setMaxOccupancy(maxOccupancy);
            } else {
                throw new IllegalArgumentException("參數異常：最大入住人數無效");
            }
        }

        // 更新库存，确保库存为非负数
        if (stock >= 0) {
            product.setStock(stock);
        } else {
            throw new IllegalArgumentException("參數異常：庫存數量無效");
        }

        // 保存更新后的产品信息
        productDao.save(product);
        return product;
    }


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

        // changeId 跟 companyId 應該要從登入中的使用者取得 (也就是從 UserHolder 工具中取出)
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：使用者未登入或身份不屬於商家");
        }

        // 打印登入的公司 ID 和變更 ID
        System.out.println("公司ID: " + loginUser.getId() + ", 變更ID: " + loginUser.getId());

        // 將商品存入資料庫
        Product newProduct = new Product();
        BeanUtils.copyProperties(addProductRequest, newProduct);
        newProduct.setCompanyId(loginUser.getId());
        newProduct.setChangeId(loginUser.getId());

        // 保存商品
        Product savedProduct = productDao.save(newProduct);

        // 打印保存的產品資料
        System.out.println("保存的產品: " + savedProduct);

        // 取得剛剛存入的商品 ID
        Integer productId = savedProduct.getProductId();

        // 處理 ProductDetails（商品詳細信息）
        ProductDetails productDetails = addProductRequest.getProductDetails();
        if (productDetails != null) {
            // 設定 productId 並存入資料庫
            productDetails.setProductId(productId);
            productDetailsDao.save(productDetails);
            System.out.println("保存的產品細節: " + productDetails);
        }

        // 處理 ProductFacilities（商品設施）
        List<ProductFacilities> productFacilities = addProductRequest.getProductFacilities();
        if (productFacilities != null && !productFacilities.isEmpty()) {
            // 設定 productId 並批量保存設施
            productFacilities.forEach(facility -> facility.setProductId(productId));
            productFacilitiesDao.saveAll(productFacilities);
            System.out.println("保存的產品設施: " + productFacilities);
        }

        // 處理圖片上傳
        ImageUploadRequest imageUploadRequest = addProductRequest.getImageUploadRequest();
        if (imageUploadRequest != null && imageUploadRequest.getFile() != null && !imageUploadRequest.getFile().isEmpty()) {
            // 確認圖片存在且非空才處理上傳
            Image image = imageService.upload(imageUploadRequest);
            Image savedImage = imageService.save(image);
            String url = "image/" + savedImage.getId();

            // 保存圖片到 ProductPhotos
            ProductPhotos productPhotos = new ProductPhotos();
            productPhotos.setProductId(productId);
            productPhotos.setPhotoUrl(url);
            productPhotos.setDescription(savedImage.getComment());
            productPhotos.setMain(true);
            productPhotosDao.save(productPhotos);

            // 打印保存的產品圖片
            System.out.println("保存的產品圖片: " + productPhotos);
        } else {
            // 沒有圖片上傳的處理
            System.out.println("沒有圖片上傳");
        }

        // 打印返回的產品
        System.out.println("返回的產品: " + newProduct);

        return savedProduct;
    }


    /**
     * 查看全部的商品型態
     **/
    @Override
    public List<Product> getAllProductTypes() {

        // 權限檢查，如果當前用戶沒有登錄或登錄的用戶角色不是管理員
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
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
        if ( loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：未登入或無查看所有產品類型的權限");
        }

        // 從資料庫中根據 productId 查詢產品
        Optional<Product> product = productDao.findById(Math.toIntExact(productId));

        // 直接返回查詢結果
        return product;
    }

    /**
     * 查看所有商品
     **/
    @Override
    public Result getAllProducts() {
        // 取得登录的用户
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null) {
            return Result.fail("用戶未登錄!");
        }
        try {
            List<Product> res = null;
            switch (loginUser.getRole()) {
                case ROLE_COMPANY:
                    res = productDao.getProductsByCompanyId(loginUser.getId());
                    break;
                case ROLE_ADMIN:
                    res = productDao.findAll();
                    break;
                default:
                    return Result.fail("用戶角色怪怪的");
            }
            return Result.ok(res, res == null ? 0L : (long) res.size());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
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
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
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
