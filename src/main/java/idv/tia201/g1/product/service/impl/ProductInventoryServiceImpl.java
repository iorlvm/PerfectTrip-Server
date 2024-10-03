package idv.tia201.g1.product.service.impl;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.product.dao.ProductDao;
import idv.tia201.g1.product.dao.ProductInventoryDao;
import idv.tia201.g1.product.dto.ProductRequest;
import idv.tia201.g1.product.entity.Product;
import idv.tia201.g1.product.exception.ResourceNotFoundException;
import idv.tia201.g1.product.service.ProductInventoryService;
import idv.tia201.g1.search.dto.ProductCalculation;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static idv.tia201.g1.core.utils.Constants.ROLE_ADMIN;
import static idv.tia201.g1.core.utils.Constants.ROLE_COMPANY;

@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Autowired
    protected ProductInventoryDao productInventoryDao;
    protected ProductDao productDao;

    // 使用構造函數注入
    public ProductInventoryServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Result getAllInventories() {

        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null) {
            return Result.fail("用戶未登錄!");
        }
        try {
            List<Product> res = null;
            switch (loginUser.getRole()) {
                case ROLE_COMPANY:
                    res = productInventoryDao.getProductsByCompanyId(loginUser.getId());  // Corrected instance method call
                    break;
                case ROLE_ADMIN:
                    res = productInventoryDao.findAll();  // Corrected instance method call
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
    public Product updateProduct(Product product) {
        Integer productId = product.getProductId();
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Id不符合規定");
        }

        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalArgumentException("未符合查閱資格");
        }

        // Corrected Optional<Product> and removed unnecessary casting
        Product existingProduct = productInventoryDao.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到相關產品"));

        Integer stock = product.getStock();
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("不合邏輯的庫存數量");
        }
        existingProduct.setStock(stock);

        Integer price = product.getPrice();
        if (price != null && price > 0) {
            existingProduct.setPrice(price);
        } else {
            throw new IllegalArgumentException("不合邏輯的價格");
        }

        productInventoryDao.save(existingProduct);  // Corrected instance method call

        return existingProduct;
    }

    @Override
    public void deleteProduct(Integer id) {

        // 參數檢查：確保 productId 不為 null 且有效
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("參數異常：產品ID無效");
        }

        // 權限檢查：確保用戶具有刪除產品的權限
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態異常：未登入或無刪除產品的權限");
        }

        // 確認要刪除的產品是否存在
        Optional<Product> theProduct = productInventoryDao.findById(id);  // Corrected Optional<Product>
        // 不存在就回傳一個失敗值
        if (!theProduct.isPresent()) {
            throw new IllegalStateException("操作失敗：產品不存在");
        }

        productInventoryDao.deleteById(id);  // Corrected instance method call
    }

    @Override
    public List<Product> getInventoriesByStatus(String status) {
        UserAuth loginUser = UserHolder.getUser();

        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalArgumentException("未符合查閱資格");
        }

        List<Product> products;
        if (status.equals("可用")) {
            products = productInventoryDao.findByStockGreaterThan(0);  // 查詢庫存大於 0 的房間
        } else if (status.equals("已預訂")) {
            products = productInventoryDao.findByStockEquals(0);  // 查詢庫存等於 0 的房間
        } else {
            products = productInventoryDao.findAll();  // 查詢所有房間
        }

        return products;
    }



    @Override
    public Product addInventory(ProductRequest productRequest) {
        // 請求參數檢查
        if (productRequest == null) {
            throw new IllegalArgumentException("參數異常：請求對象為 null");
        }

        if (StringUtils.isBlank(productRequest.getProductName())) {
            throw new IllegalArgumentException("參數異常：商品名稱未填寫");
        }

        if (productRequest.getPrice() == null) {
            throw new IllegalArgumentException("參數異常：價格未填寫");
        }

        if (productRequest.getStock() == null) {
            throw new IllegalArgumentException("參數異常：庫存未填寫");
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
        BeanUtils.copyProperties(productRequest, newProduct);
        newProduct.setCompanyId(loginUser.getId());
        newProduct.setChangeId(loginUser.getId());

        // 保存商品
        Product savedProduct = productDao.save(newProduct);

        // 打印保存的產品資料
        System.out.println("保存的產品: " + savedProduct);

        // 取得剛剛存入的商品 ID
        Integer productId = savedProduct.getProductId();

        return savedProduct;
    }

    @Override
    public List<Product> getInventoryByDateRange(LocalDate startDate, LocalDate endDate) {
        // 確保正確的日期範圍
        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        System.out.println("查詢範圍: " + start + " 到 " + end);

        // 獲取當前登入用戶的 companyId
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalArgumentException("未符合查閱資格");
        }
        Integer companyId = loginUser.getId();  // 獲取當前商家的 companyId

        // 如果有有效的公司 ID，將其添加到列表中
        List<Integer> companyIds = List.of(companyId);  // 傳遞商家ID到查詢
        Map<Integer, List<ProductCalculation>> productCalculationsMap = productInventoryDao.getProductCalculations(companyIds, start, end);

        // 確認查詢結果
        System.out.println("查詢到的產品數量: " + productCalculationsMap.size());

        // 將 Map 中的所有 ProductCalculation 對象轉換成 Product 對象
        return productCalculationsMap.values().stream()
                .flatMap(List::stream)
                .map(pc -> {
                    Product product = new Product();
                    product.setProductId(pc.getProductId());
                    product.setProductName(pc.getProductName());
                    product.setMaxOccupancy(pc.getMaxOccupancy());
                    product.setStock(pc.getRemainingRooms());  // 使用 remainingRooms 作為庫存數量
                    product.setPrice(pc.getPrice());
                    return product;
                })
                .collect(Collectors.toList());
    }


}


