package idv.tia201.g1.search.dao.impl;

import idv.tia201.g1.search.dao.SearchDao;
import idv.tia201.g1.search.dto.ProductCalculation;
import idv.tia201.g1.search.dto.SearchResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SearchDaoImpl implements SearchDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Integer> findCompanyIdsByCityOrCountry(String destination) {
        String queryStr = "SELECT c.company_id " +
                "FROM company_master c " +
                "WHERE c.city LIKE :destination " +
                "OR c.country LIKE :destination " +
                "OR c.company_name LIKE :destination;";

        Query query = entityManager.createNativeQuery(queryStr);

        String likePattern = "%" + destination + "%";
        query.setParameter("destination", likePattern);

        @SuppressWarnings("unchecked")
        List<Integer> results = query.getResultList();

        return results;
    }

    @Override
    public Map<Integer, List<ProductCalculation>> getProductCalculations(List<Integer> companyIds, Date startDate, Date endDate) {
        String queryStr = "SELECT " +
                "    p.company_id, " +                                              // 商家編號
                "    p.product_id, " +                                              // 商品編號
                "    p.max_occupancy, " +                                           // 幾人房
                "    p.stock - COALESCE( " +
                "            ( " +
                "                SELECT MAX(daily.booked) " +                       // 找出每日的最大預訂量
                "                FROM ( " +
                "                         SELECT SUM(od.quantity) AS booked " +
                "                         FROM order_detail od " +
                "                         WHERE od.product_id = p.product_id " +
                "                           AND od.booked_date >= :startDate " +    // 訂單的開始日期
                "                           AND od.booked_date < :endDate " +       //訂單的結束日期
                "                           AND od.expired_time > NOW() " +
                "                         GROUP BY od.booked_date " +               // 按日期分組計算每天的訂單數量
                "                     ) AS daily " +
                "            ), 0 " +
                "    ) AS remaining_rooms, " +                                      // 剩餘房間數量
                "    p.price AS price, " +
                "    p.product_name " +                                           // 價格 (原價)
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
            ProductCalculation productCalculation = getProductCalculation(res);

            List<ProductCalculation> productCalculations = companyProductMap.computeIfAbsent(companyId, k -> new ArrayList<>());

            productCalculations.add(productCalculation);
        }

        return companyProductMap;
    }

    @Override
    public SearchResponse getDetailsByProductIds(List<Integer> productIds) {
        String queryStr = "SELECT " +
                "   COALESCE(MIN(pd.includes_breakfast), 0), " +
                "   COALESCE(MIN(pd.allow_date_changes), 0), " +
                "   COALESCE(MIN(pd.allow_free_cancellation), 0), " +
                "   COALESCE(MIN(pd.is_refundable), 0) " +
                "FROM product_master p " +
                "LEFT JOIN product_details pd ON p.product_id = pd.product_id " +
                "WHERE p.product_id IN :productIds";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("productIds", productIds);

        // 獲取結果
        Object[] row = (Object[]) query.getSingleResult();  // 確保只有一筆結果

        // 將結果封裝到 SearchResponse
        SearchResponse response = new SearchResponse();

        // 按順序映射到 SearchResponse
        response.setIncludesBreakfast(((Number) row[0]).intValue() == 1);
        response.setAllowDateChanges(((Number) row[1]).intValue() == 1);
        response.setAllowFreeCancellation(((Number) row[2]).intValue() == 1);
        response.setIsRefundable(((Number) row[3]).intValue() == 1);

        return response;
    }

    @Override
    public List<Integer> getHotCompanyIds(Integer size) {
        String queryStr = "SELECT cm.company_id " +
                "FROM company_master cm " +
                "ORDER BY cm.score DESC LIMIT :size";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("size", size);

        @SuppressWarnings("unchecked")
        List<Object> results = query.getResultList();

        List<Integer> companyIds = new ArrayList<>();
        for (Object result : results) {
            companyIds.add(((Number) result).intValue());
        }

        return companyIds;
    }

    @Override
    public List<Integer> getRandCompanyIds(Integer size) {
        String queryStr = "SELECT cm.company_id " +
                "FROM company_master cm " +
                "ORDER BY RAND() LIMIT :size";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("size", size);

        @SuppressWarnings("unchecked")
        List<Object> results = query.getResultList();

        List<Integer> companyIds = new ArrayList<>();
        for (Object result : results) {
            companyIds.add(((Number) result).intValue());
        }

        return companyIds;
    }

    @Override
    public List<Integer> getDiscountCompanyIds(Date date, Integer size) {
        String queryStr = "SELECT cm.company_id " +
                "FROM company_master cm " +
                "JOIN product_discount pd ON cm.company_id = pd.company_id " +
                "WHERE :date BETWEEN pd.start_date_time AND pd.end_date_time " +
                "GROUP BY cm.company_id " +
                "ORDER BY MIN(pd.discount_rate) " +
                "LIMIT :size";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("size", size);
        query.setParameter("date", date);

        @SuppressWarnings("unchecked")
        List<Object> results = query.getResultList();

        List<Integer> companyIds = new ArrayList<>();
        for (Object result : results) {
            companyIds.add(((Number) result).intValue());
        }

        return companyIds;
    }

    // 封裝轉換用回傳格式
    private static ProductCalculation getProductCalculation(Object[] res) {
        Integer productId = (Integer) res[1];
        Integer maxOccupancy = (Integer) res[2];
        BigDecimal bigDecimalValue = (BigDecimal) res[3];
        Integer remainingRooms = bigDecimalValue.intValue();
        Integer price = (Integer) res[4];
        String productName = (String) res[5];

        ProductCalculation productCalculation = new ProductCalculation();
        productCalculation.setProductId(productId);
        productCalculation.setMaxOccupancy(maxOccupancy);
        productCalculation.setRemainingRooms(remainingRooms);
        productCalculation.setPrice(price);
        productCalculation.setProductName(productName);
        return productCalculation;
    }
}
