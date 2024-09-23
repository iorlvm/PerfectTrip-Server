package idv.tia201.g1.search.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.tia201.g1.member.dao.CompanyDao;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.order.dao.OrderDao;
import idv.tia201.g1.order.uitls.OrderUtil;
import idv.tia201.g1.product.dao.ProductDetailsDao;
import idv.tia201.g1.product.entity.ProductDetails;
import idv.tia201.g1.product.entity.ProductPhotos;
import idv.tia201.g1.search.dao.SearchDao;
import idv.tia201.g1.search.dto.ProductCalculation;
import idv.tia201.g1.search.dto.SearchProductResponse;
import idv.tia201.g1.search.dto.SearchRequest;
import idv.tia201.g1.search.dto.SearchResponse;
import idv.tia201.g1.search.service.SearchService;
import idv.tia201.g1.search.utils.SearchUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static idv.tia201.g1.core.utils.Constants.*;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Long LOCK_TTL = 10L;

    @Autowired
    private SearchDao searchDao;
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDetailsDao productDetailsDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Page<SearchResponse> search(SearchRequest request) {
        // 標準請求驗證
        validateRequest(request);

        // 追加驗證
        if (request.getDestination() == null || request.getDestination().isEmpty()) {
            throw new IllegalArgumentException("目的地(destination)為必填項");
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

        List<SearchResponse> responses = getCachedResponses(key);

        // 緩存中不存在資料
        if (responses.isEmpty()) {
            // 嘗試取得鎖
            while (!tryLock(key)) {
                // 取得鎖成功失敗  休眠後重試
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                // 取得鎖成功 重新檢查是否已重建緩存
                responses = getCachedResponses(key);

                if (responses.isEmpty()) {
                    // 還是沒有緩存時, 查詢並計算
                    responses = searchAndCalculateProductDetails(
                            request.getDestination(),
                            request.getAdultCount(),
                            request.getRoomCount(),
                            request.getStartDate(),
                            request.getEndDate()
                    );

                    // 將結果緩存
                    cacheResponses(key, responses);
                }
            } finally {
                // 解鎖
                unlock(key);
            }
        }

        stringRedisTemplate.expire(key, CACHE_SEARCH_TTL, TimeUnit.SECONDS);        // 十分鐘過期消失  (設定/重設過期時間)

        // 排序
        sortResponses(responses, request.getOrderBy(), request.getIsDesc());

        // 分頁返回
        return getPageResponse(responses, page, pageSize, pageRequest);
    }

    @Override
    public List<SearchProductResponse> searchProductListByCompanyId(Integer companyId, SearchRequest searchRequest) {
        // 驗證請求格式
        validateRequest(searchRequest);

        Date startDate = searchRequest.getStartDate();
        Date endDate = searchRequest.getEndDate();
        long daysBetween = OrderUtil.getDaysBetween(startDate, endDate);

        Map<Integer, List<ProductCalculation>> res = searchDao.getProductCalculations(
                Collections.singletonList(companyId),
                startDate,
                endDate
        );
        List<ProductCalculation> productCalculations = res.get(companyId);

        productCalculations = productCalculations.stream()
                .filter(product -> product.getRemainingRooms() >= 1)
                .toList();

        List<Double> discount = OrderUtil.getDiscountByCompanyIdBetweenStartDateAnEndDate(orderDao, companyId, startDate, endDate);

        // 把列表轉為Map方便進行後續的查詢操作
        Map<Integer, ProductCalculation> productCalculationMap = new HashMap<>();
        for (ProductCalculation productCalculation : productCalculations) {
            double totalPrice = 0;
            for (Double v : discount) {
                totalPrice += productCalculation.getPrice() * v;
            }
            productCalculation.setPrice((int) totalPrice);
            productCalculationMap.put(productCalculation.getProductId(), productCalculation);
        }

        List<Integer> productIds = productCalculationMap.keySet().stream().toList();

        List<ProductDetails> productDetails = productDetailsDao.findByProductIdIn(productIds);

        List<SearchProductResponse> productResponses = new ArrayList<>(productDetails.size());
        for (ProductDetails productDetail : productDetails) {
            ProductCalculation productCalculation = productCalculationMap.get(productDetail.getProductId());
            SearchProductResponse searchProductResponse = createSearchProductResponse(productDetail, productCalculation);
            searchProductResponse.setDays((int) daysBetween);

            productResponses.add(searchProductResponse);
        }

        // 將結果排序後回傳
        productResponses.sort(Comparator
                .comparing(SearchProductResponse::getMaxOccupancy)
                .thenComparing(SearchProductResponse::getPrice));

        return productResponses;
    }

    @Override
    public void deleteSearchCache(SearchRequest request) {
        // 標準請求驗證
        validateRequest(request);

        // 追加驗證
        if (request.getDestination() == null || request.getDestination().isEmpty()) {
            throw new IllegalArgumentException("目的地(destination)為必填項");
        }

        int adultCount = request.getAdultCount();
        int roomCount = request.getRoomCount();
        Date startDate = request.getStartDate();
        Date endDate = request.getEndDate();
        String destination = request.getDestination();

        String key = CACHE_SEARCH_PREFIX + destination + ":" + adultCount + ":" + roomCount + ":" + startDate + ":" + endDate;

        stringRedisTemplate.delete(key);
    }

    private SearchProductResponse createSearchProductResponse(ProductDetails productDetails, ProductCalculation productCalculation) {
        SearchProductResponse searchProductResponse = new SearchProductResponse();
        BeanUtils.copyProperties(productDetails, searchProductResponse);
        BeanUtils.copyProperties(productCalculation, searchProductResponse);
        List<ProductPhotos> photos = searchProductResponse.getPhotos();
        // 將主圖排到第一張
        photos.sort((a, b) -> Boolean.compare(b.getIsMain(), a.getIsMain()));
        // 對圖片添加前綴
        for (ProductPhotos photo : photos) {
            photo.setPhotoUrl(BASE_URL + photo.getPhotoUrl());
        }
        return searchProductResponse;
    }

    private void validateRequest(SearchRequest request) {
        if (request.getAdultCount() == null) {
            throw new IllegalArgumentException("成人數(adultCount)為必填項");
        }
        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("開始日期(startDate)為必填項");
        }
        if (request.getEndDate() == null) {
            throw new IllegalArgumentException("結束日期(endDate)為必填項");
        }
        if (request.getRoomCount() == null) {
            throw new IllegalArgumentException("房間數(roomCount)為必填項");
        }
    }

    private List<SearchResponse> getCachedResponses(String key) {
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
        }

        return responses;
    }

    private void cacheResponses(String key, List<SearchResponse> responses) {
        if (responses == null || responses.isEmpty()) return;
        // 將處理完的responses存到redis中緩存
        for (SearchResponse response : responses) {
            try {
                stringRedisTemplate.opsForList().leftPush(key, objectMapper.writeValueAsString(response));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }          // 存到緩存避免短時間內重新查詢 (創建訂單時同時刪除緩存)
    }

    private List<SearchResponse> searchAndCalculateProductDetails(String destination, int adultCount, int roomCount, Date startDate, Date endDate) {
        List<SearchResponse> responses = new ArrayList<>();

        // 查詢符合目的地的商家
        List<Integer> companyIds = searchDao.findCompanyIdsByCityOrCountry(destination);
        if (companyIds.isEmpty()) return responses;

        Map<Integer, List<ProductCalculation>> productCalculations = searchDao.getProductCalculations(companyIds, startDate, endDate);
        Set<Integer> processedCompanies = new HashSet<>();

        for (Map.Entry<Integer, List<ProductCalculation>> entry : productCalculations.entrySet()) {
            Integer companyId = entry.getKey();

            if (processedCompanies.contains(companyId)) continue;
            processedCompanies.add(companyId);

            List<ProductCalculation> products = entry.getValue();
            int totalRemainingRooms = products.stream().mapToInt(ProductCalculation::getRemainingRooms).sum();
            int totalMaxOccupancy = products.stream().mapToInt(product -> product.getMaxOccupancy() * product.getRemainingRooms()).sum();

            if (totalRemainingRooms < roomCount || totalMaxOccupancy < adultCount) continue;

            SearchUtils.ProductSet minCost = SearchUtils.findMinCost(products, adultCount, roomCount);
            if (minCost.getMinCost() >= 0) {
                responses.add(createSearchResponse(companyId, minCost, startDate, endDate));
            }
        }

//        System.out.println(responses);
        return responses;
    }

    private SearchResponse createSearchResponse(Integer companyId, SearchUtils.ProductSet minCost, Date startDate, Date endDate) {
        SearchResponse searchResponse = searchDao.getDetailsByProductIds(minCost.getProductIds());
        Company company = companyDao.findByCompanyId(companyId);

        searchResponse.setProducts(minCost.getProductNames());
        searchResponse.setCompanyId(companyId);
        searchResponse.setCompanyName(company.getCompanyName());
        searchResponse.setCity(company.getCity());
        searchResponse.setCountry(company.getCountry());
        searchResponse.setPhoto(BASE_URL + "image/74721697827127301");   // TODO: 靜態寫入 等entity更新
        searchResponse.setScore(company.getScore());
        searchResponse.setCommentCount(999);                             // TODO: 靜態寫入 等entity更新

        List<Double> discounts = OrderUtil.getDiscountByCompanyIdBetweenStartDateAnEndDate(orderDao, companyId, startDate, endDate);

        boolean isPromotion = false;
        double totalPrice = 0;
        for (Double discount : discounts) {
            if (discount < 1.0) isPromotion = true;
            totalPrice += discount * minCost.getMinCost();
        }

        searchResponse.setIsPromotion(isPromotion);
        searchResponse.setPrice((int) totalPrice);

        return searchResponse;
    }

    private void sortResponses(List<SearchResponse> responses, String orderBy, Boolean isDesc) {
        if (orderBy == null) orderBy = "price";
        Comparator<SearchResponse> comparator;

        switch (orderBy) {
            case "score":
                comparator = Comparator.comparingDouble(SearchResponse::getScore);
                break;
            case "price":
            default:
                comparator = Comparator.comparingDouble(SearchResponse::getPrice);
                break;
        }

        if (isDesc != null && isDesc) {
            comparator = comparator.reversed();
        }

        responses.sort(comparator);
    }

    private Page<SearchResponse> getPageResponse(List<SearchResponse> responses, int page, int pageSize, PageRequest pageRequest) {
        int total = responses.size();
        int offset = page * pageSize;
        int end = Math.min(offset + pageSize, total);

        if (offset >= end) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, total);
        }

        return new PageImpl<>(responses.subList(offset, end), pageRequest, total);
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock:" + key, "lock", LOCK_TTL, TimeUnit.SECONDS);
        return flag != null && flag;
    }

    private void unlock(String key) {
        stringRedisTemplate.delete("lock:" + key);
    }
}
