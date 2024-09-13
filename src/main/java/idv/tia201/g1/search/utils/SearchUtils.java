package idv.tia201.g1.search.utils;

import idv.tia201.g1.search.dto.ProductCalculation;
import lombok.Data;

import java.util.*;

public class SearchUtils {
    @Data
    public static class ProductSet {
        int minCost;
        List<Integer> productIds;

        public ProductSet(int minCost, List<Integer> productIds) {
            this.minCost = minCost;
            this.productIds = productIds;
        }
    }

    public static ProductSet findMinCost(List<ProductCalculation> products, int minAdults, int requiredRooms) {
        int n = products.size();
        int minCost = Integer.MAX_VALUE;
        List<Integer> bestProductIds = Collections.emptyList();

        // 生成所有可能的房間組合
        int[] currentCombination = new int[n];
        while (true) {
            // 計算當前組合的總價格、容納人數和房間數量
            int totalCost = 0;
            int roomsUsed = 0;
            int adultsAccommodated = 0;
            List<Integer> currentProductIds = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int count = currentCombination[i];
                if (count > 0) {
                    ProductCalculation product = products.get(i);
                    totalCost += count * product.getPrice();
                    roomsUsed += count;
                    adultsAccommodated += count * product.getMaxOccupancy();
                    for (int j = 0; j < count; j++) {
                        currentProductIds.add(product.getProductId());
                    }
                }
            }

            // 檢查是否滿足要求
            if (roomsUsed == requiredRooms && adultsAccommodated >= minAdults) {
                if (totalCost < minCost) {
                    minCost = totalCost;
                    bestProductIds = currentProductIds;
                }
            }

            // 生成下一個組合
            int i = 0;
            while (i < n && currentCombination[i] == products.get(i).getRemainingRooms()) {
                currentCombination[i] = 0;
                i++;
            }
            if (i == n) {
                break;
            }
            currentCombination[i]++;
        }

        // 返回結果
        return minCost == Integer.MAX_VALUE ? new ProductSet(-1, Collections.emptyList()) : new ProductSet(minCost, bestProductIds);
    }
}
