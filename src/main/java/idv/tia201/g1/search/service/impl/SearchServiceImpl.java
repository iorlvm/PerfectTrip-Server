package idv.tia201.g1.search.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.tia201.g1.member.dao.CompanyDao;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.order.dao.OrderDao;
import idv.tia201.g1.order.uitls.OrderUitl;
import idv.tia201.g1.search.dao.SearchDao;
import idv.tia201.g1.search.dto.ProductCalculation;
import idv.tia201.g1.search.dto.SearchRequest;
import idv.tia201.g1.search.dto.SearchResponse;
import idv.tia201.g1.search.service.SearchService;
import idv.tia201.g1.search.utils.SearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static idv.tia201.g1.core.utils.Constants.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchDao searchDao;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Page<SearchResponse> search(SearchRequest request) {
        if (request.getAdultCount() == null) {
            throw new IllegalArgumentException("成人數(adultCount)為必填項");
        }
        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("開始日期(startDate)為必填項");
        }
        if (request.getEndDate() == null) {
            throw new IllegalArgumentException("結束日期(endDate)為必填項");
        }
        if (request.getDestination() == null || request.getDestination().isEmpty()) {
            throw new IllegalArgumentException("目的地(destination)為必填項");
        }
        if (request.getRoomCount() == null) {
            throw new IllegalArgumentException("房間數(roomCount)為必填項");
        }

        Integer page = request.getPage();
        Integer pageSize = request.getSize();

        PageRequest pageRequest = PageRequest.of(page, pageSize);

        int adultCount = request.getAdultCount();
        int roomCount = request.getRoomCount();
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        String destination = request.getDestination();

        String key = CACHE_SEARCH_PREFIX + destination + ":" + adultCount + ":" + roomCount + ":" + startDate + ":" + endDate;

        List<SearchResponse> responses = new ArrayList<>();
        Long size = stringRedisTemplate.opsForList().size(key);

        if (size != null && size > 0) {
            List<String> jsonList = stringRedisTemplate.opsForList().range(key, 0, size - 1);

            assert jsonList != null;
            for (String json : jsonList) {
                try {
                    SearchResponse searchResponse = objectMapper.readValue(json, SearchResponse.class);
                    responses.add(searchResponse);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {
            // 取得地點符合條件的商家id列表
            List<Integer> companyIds = searchDao.findCompanyIdsByCityOrCountry(destination);

            if (companyIds.isEmpty()) return new PageImpl<>(Collections.emptyList(), pageRequest, 0);

            // 根據商家id列表取得剩餘的房間數量以及價格
            Map<Integer, List<ProductCalculation>> productCalculations = searchDao.getProductCalculations(companyIds, startDate, endDate);


            for (Map.Entry<Integer, List<ProductCalculation>> entry : productCalculations.entrySet()) {
                Integer companyId = entry.getKey();
                List<ProductCalculation> products = entry.getValue();

                // 計算總剩餘房間數量和總可住宿人數 (先排除不可能的選項)
                int totalRemainingRooms = products.stream()
                        .mapToInt(ProductCalculation::getRemainingRooms)
                        .sum();

                int totalMaxOccupancy = products.stream()
                        .mapToInt(product -> product.getMaxOccupancy() * product.getRemainingRooms())
                        .sum();

                boolean isValidCompany = totalRemainingRooms >= roomCount &&
                        totalMaxOccupancy >= adultCount;

                if (!isValidCompany) {
                    continue;
                }

                SearchUtils.ProductSet minCost = SearchUtils.findMinCost(products, adultCount, roomCount);
                int minPrice = minCost.getMinCost();
                System.out.println(minCost);
                if (minPrice >= 0) {
                    // 合理的價格, 表示有找到資料
                    List<Integer> productIds = minCost.getProductIds();

                    SearchResponse searchResponse = searchDao.getDetailsByProductIds(productIds);
                    Company company = companyDao.findByCompanyId(companyId);

                    searchResponse.setCompanyId(companyId);
                    searchResponse.setCompanyName(company.getCompanyName());
                    searchResponse.setCity("台北");               // TODO: 靜態寫入 等entity更新
                    searchResponse.setCountry("台灣");            // TODO: 靜態寫入 等entity更新
                    searchResponse.setPhoto(BASE_URL + "image/74721697827127301"); // TODO: 靜態寫入 等待其他組員更新
                    searchResponse.setScore(company.getScore());
                    searchResponse.setCommentCount(999);         // TODO: 靜態寫入 等待其他組員更新

                    // 取得日期範圍的折扣列表
                    List<Double> discounts = OrderUitl.getDiscountByCompanyIdBetweenStartDateAnEndDate(orderDao, companyId, startDate, endDate);

                    boolean isPromotion = false;
                    double totalPrice = 0;
                    for (Double discount : discounts) {
                        if (discount < 1.0) isPromotion = true;
                        totalPrice += discount * minPrice;
                    }

                    searchResponse.setIsPromotion(isPromotion);
                    searchResponse.setPrice((int) totalPrice);

                    responses.add(searchResponse);
                }
            }

            // 將處理完的responses存到redis中緩存
            if (!responses.isEmpty()) {
                List<String> jsonList = new ArrayList<>(responses.size());
                for (SearchResponse response : responses) {
                    try {
                        jsonList.add(objectMapper.writeValueAsString(response));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                stringRedisTemplate.opsForList().leftPushAll(key, jsonList);            // 存到緩存避免短時間內重新查詢 (創建訂單時同時刪除緩存)
            }
        }
        stringRedisTemplate.expire(key, CACHE_SEARCH_TTL, TimeUnit.SECONDS);        // 十分鐘過期消失  (設定/重設過期時間)

        // 根據價格由低到高排序  回傳Page<SearchResponse>
        String orderBy = request.getOrderBy();
        Boolean isDesc = request.getIsDesc();

        if (orderBy == null) orderBy = "price";

        switch (orderBy) {
            case "score":
                if (!isDesc) responses.sort(Comparator.comparingDouble(SearchResponse::getScore));
                else responses.sort(Comparator.comparingDouble(SearchResponse::getScore).reversed());
                break;
            case "price":
            default:
                if (!isDesc) responses.sort(Comparator.comparingDouble(SearchResponse::getPrice));
                else responses.sort(Comparator.comparingDouble(SearchResponse::getPrice).reversed());
                break;
        }

        int total = responses.size();
        int offset = page * pageSize;
        int end = Math.min(offset + pageSize, responses.size());

        if (offset >= end) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, total);
        }

        return new PageImpl<>(responses.subList(offset, end), pageRequest, total);
    }
}
