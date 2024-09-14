package idv.tia201.g1.product.service;

import idv.tia201.g1.product.entity.ProductDiscount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductDiscountService {

    ProductDiscount addProductDiscount(ProductDiscount productDiscount);

    List<ProductDiscount> getByCompanyId(Integer companyId);

    ProductDiscount updateDiscount(Integer discountId, ProductDiscount productDiscount);

    void deleteProductDiscount(Integer discountId);
}
