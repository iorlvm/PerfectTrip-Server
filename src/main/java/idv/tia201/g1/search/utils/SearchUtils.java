package idv.tia201.g1.search.utils;

import idv.tia201.g1.search.dto.ProductCalculation;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
        int totalCost = 0;
        int adultsAccommodated = 0;
        int roomsUsed = 0;
        List<Integer> selectedProductIds = new ArrayList<>();


        // 每次選擇房間後重新排序
        while (adultsAccommodated < minAdults && roomsUsed < requiredRooms) {
            int remainingAdults = minAdults - adultsAccommodated;
            products.sort(((o1, o2) -> {
                        // 計算房間容量與需求的差異
                        int diff1 = Math.abs(o1.getMaxOccupancy() - remainingAdults);
                        int diff2 = Math.abs(o2.getMaxOccupancy() - remainingAdults);

                        // 先比較差異大小
                        if (diff1 != diff2) {
                            return diff1 - diff2;
                        } else {
                            // 如果差異相同，根據性價比 (價格 / 可容納人數) 進行比較
                            double ratio1 = (double) o1.getPrice() / o1.getMaxOccupancy();
                            double ratio2 = (double) o2.getPrice() / o2.getMaxOccupancy();
                            return Double.compare(ratio1, ratio2);
                        }
                    }));

            // 2. 選擇排序後的第一間房間
            ProductCalculation bestProduct = products.get(0);

            // 3. 確保不超過房間庫存
            if (bestProduct.getRemainingRooms() <= 0) {
                // 沒有可用房間了，跳過此房型
                products.remove(0);
                continue;
            }

            // 4. 選擇一間房間，更新成本、成人數和房間使用數
            totalCost += bestProduct.getPrice();
            adultsAccommodated += bestProduct.getMaxOccupancy();
            roomsUsed++;

            // 記錄選擇的房間ID
            selectedProductIds.add(bestProduct.getProductId());

            // 5. 更新剩餘房間數量
            bestProduct.setRemainingRooms(bestProduct.getRemainingRooms() - 1);

            // 6. 如果該房型的剩餘房間已用完，從列表中移除
            if (bestProduct.getRemainingRooms() == 0) {
                products.remove(0);
            }
        }

        // 7. 檢查是否滿足最小成人數的需求
        if (adultsAccommodated < minAdults) {
            return new ProductSet(-1, new ArrayList<>());  // 無法滿足需求
        }

        // 返回最小成本和選擇的房間ID
        return new ProductSet(totalCost, selectedProductIds);
    }
}
