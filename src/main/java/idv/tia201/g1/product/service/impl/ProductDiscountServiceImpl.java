package idv.tia201.g1.product.service.impl;

import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.product.dao.ProductDiscountDao;
import idv.tia201.g1.product.entity.ProductDiscount;
import idv.tia201.g1.product.exception.ResourceNotFoundException;
import idv.tia201.g1.product.service.ProductDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static idv.tia201.g1.core.utils.Constants.ROLE_COMPANY;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDiscountDao productDiscountDao;

    @Autowired
    public ProductDiscountServiceImpl(ProductDiscountDao productDiscountDao) {
        this.productDiscountDao = productDiscountDao;
    }

    @Override
    public ProductDiscount addProductDiscount(ProductDiscount productDiscount) {

        if (productDiscount == null ||
                !StringUtils.hasText(productDiscount.getDiscountTitle())) {
            throw new IllegalArgumentException("填寫錯誤：優惠標題未填寫！");
        }
        if (productDiscount.getDiscountRate() == null ||
                productDiscount.getDiscountRate() <= 0) {
            throw new IllegalArgumentException("填寫錯誤：優惠折扣無效！");
        }
        if (productDiscount.getStartDateTime() == null ||
                productDiscount.getEndDateTime() == null) {
            throw new IllegalArgumentException("填寫錯誤：開始和結束日期須填寫！");
        }

        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_COMPANY.equals(loginUser.getRole())) {
            throw new IllegalStateException("狀態錯誤：使用者未登入或身份不屬於店家！");
        }

        productDiscount.setCompanyId(loginUser.getId());
        productDiscount.setChangeId(loginUser.getId());

        return productDiscountDao.save(productDiscount);
    }

    @Override
    public List<ProductDiscount> getByCompanyId(Integer companyId) {
        if (companyId == null ||
                companyId <= 0) {
            throw new IllegalArgumentException("狀態錯誤：公司ID為無效！");
        }
        return productDiscountDao.findByCompanyId(companyId);
    }

    @Override
    public ProductDiscount updateDiscount(Integer discountId, ProductDiscount productDiscount) {
        if (discountId == null ||
                discountId <= 0) {
            throw new IllegalArgumentException("填寫錯誤：優惠ID為無效！");
        }

        ProductDiscount existingDiscount = productDiscountDao.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到相關優惠！"));

        if (StringUtils.hasText(productDiscount.getDiscountTitle())) {
            existingDiscount.setDiscountTitle(productDiscount.getDiscountTitle());
        }
        if (productDiscount.getDiscountRate() != null && productDiscount.getDiscountRate() > 0) {
            existingDiscount.setDiscountRate(productDiscount.getDiscountRate());
        }
        if (productDiscount.getStartDateTime() != null) {
            existingDiscount.setStartDateTime(productDiscount.getStartDateTime());
        }
        if (productDiscount.getEndDateTime() != null) {
            existingDiscount.setEndDateTime(productDiscount.getEndDateTime());
        }

        return productDiscountDao.save(existingDiscount);
    }

    @Override
    public void deleteProductDiscount(Integer discountId) {
        if (discountId == null || discountId <= 0) {
            throw new IllegalArgumentException("填寫錯誤：此優惠ID為無效！");
        }

        ProductDiscount discount = productDiscountDao.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到相關優惠！"));

        productDiscountDao.delete(discount);
    }
}
