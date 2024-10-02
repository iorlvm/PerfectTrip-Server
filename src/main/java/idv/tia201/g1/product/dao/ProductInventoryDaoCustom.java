package idv.tia201.g1.product.dao;

import idv.tia201.g1.search.dto.ProductCalculation;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * ClassName:ProductInventoryDaoCustom
 * Package: idv.tia201.g1.product.dao.impl
 * Description:
 *
 * @Author: Jacob
 * @Create: 2024/10/1 - 下午7:48
 * @Version: v1.0
 */

@Repository
public interface ProductInventoryDaoCustom {
    Map<Integer, List<ProductCalculation>> getProductCalculations(List<Integer> companyIds, Date startDate, Date endDate);
}
