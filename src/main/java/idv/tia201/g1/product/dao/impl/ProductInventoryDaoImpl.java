package idv.tia201.g1.product.dao.impl;

import idv.tia201.g1.product.dao.ProductInventoryDaoCustom;
import idv.tia201.g1.search.dto.ProductCalculation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class ProductInventoryDaoImpl implements ProductInventoryDaoCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Map<Integer, List<ProductCalculation>> getProductCalculations(List<Integer> companyIds, Date startDate, Date endDate) {

        System.out.println("公司 ID 列表: " + companyIds);
        System.out.println("查詢範圍: " + startDate + " 到 " + endDate);

        String queryStr = "SELECT " +
                "    p.company_id, " +                                                // 商家編號
                "    p.product_id, " +                                                // 商品編號
                "    p.max_occupancy, " +                                             // 幾人房
                "    p.stock - COALESCE( " +
                "            ( " +
                "                SELECT SUM(daily.booked) " +                         // 改為計算總預訂量
                "                FROM ( " +
                "                         SELECT SUM(od.quantity) AS booked " +
                "                         FROM order_detail od " +
                "                         WHERE od.product_id = p.product_id " +
                "                           AND od.booked_date >= :startDate " +      // 訂單的開始日期
                "                           AND od.booked_date < :endDate " +         // 訂單的結束日期
                "                           AND (od.expired_time IS NULL OR od.expired_time > NOW()) " +  // 過期時間檢查
                "                         GROUP BY od.booked_date " +                 // 按日期分組計算每天的訂單數量
                "                     ) AS daily " +
                "            ), 0 " +
                "    ) AS remaining_rooms, " +                                        // 剩餘房間數量
                "    p.price AS price, " +                                            // 價格
                "    p.product_name " +                                               // 商品名稱
                "FROM product_master p " +
                "WHERE p.company_id IN :companyIds";


        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("companyIds", companyIds);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        Map<Integer, List<ProductCalculation>> companyProductMap = new HashMap<>();
        for (Object[] res : results) {
            Integer companyId = (Integer) res[0];
            // 使用你自己的轉換方法代替 getProductCalculation
            ProductCalculation productCalculation = convertToProductCalculation(res);

            List<ProductCalculation> productCalculations = companyProductMap.computeIfAbsent(companyId, k -> new ArrayList<>());

            productCalculations.add(productCalculation);
        }

        return companyProductMap;
    }

    private ProductCalculation convertToProductCalculation(Object[] res) {
        ProductCalculation pc = new ProductCalculation();

        pc.setProductId((Integer) res[1]);     // 商品編號
        pc.setProductName((String) res[5]);    // 商品名稱

        // 如果 maxOccupancy 是 Integer，直接轉換
        if (res[2] instanceof Integer) {
            pc.setMaxOccupancy((Integer) res[2]);
        } else if (res[2] instanceof BigDecimal) {
            pc.setMaxOccupancy(((BigDecimal) res[2]).intValue());
        }

        // 如果 remainingRooms 是 Integer，直接轉換
        if (res[3] instanceof Integer) {
            pc.setRemainingRooms((Integer) res[3]);
        } else if (res[3] instanceof BigDecimal) {
            pc.setRemainingRooms(((BigDecimal) res[3]).intValue());
        }

        // 如果 price 是 BigDecimal，進行轉換
        if (res[4] instanceof BigDecimal) {
            pc.setPrice(((BigDecimal) res[4]).intValue());  // 將 BigDecimal 轉換為 int
        } else if (res[4] instanceof Integer) {
            pc.setPrice((Integer) res[4]);  // 如果 price 是 Integer，直接轉換
        }

        return pc;
    }


}
