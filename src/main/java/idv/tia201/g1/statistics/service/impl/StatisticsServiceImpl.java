package idv.tia201.g1.statistics.service.impl;

import idv.tia201.g1.order.dao.OrderDao;
import idv.tia201.g1.order.dao.OrderDetailDao;
import idv.tia201.g1.statistics.dto.CustomerSourceData;
import idv.tia201.g1.statistics.dto.OrderStats;
import idv.tia201.g1.statistics.dto.RoomTypeSalesData;
import idv.tia201.g1.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDetailDao orderDetailDao;

    @Override
    public List<OrderStats> getDailyOrderData() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        List<Object[]> last7DaysOrderStats = orderDao.findLast7DaysOrderStats(sevenDaysAgo);

        List<OrderStats> orderStats = new ArrayList<>(last7DaysOrderStats.size());

        for (Object[] last7DaysOrderStat : last7DaysOrderStats) {
            OrderStats orderStat = new OrderStats(last7DaysOrderStat[0], last7DaysOrderStat[1], last7DaysOrderStat[2]);
            orderStats.add(orderStat);
        }

        List<OrderStats> completeStats = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            OrderStats statsForDate = orderStats.stream()
                    .filter(stats -> stats.getOrderDate().equals(date))
                    .findFirst()
                    .orElse(new OrderStats(date, 0L, BigDecimal.ZERO));
            completeStats.add(statsForDate);
        }

        return completeStats.stream()
                .sorted(Comparator.comparing(OrderStats::getOrderDate))
                .toList();
    }


    @Override
    public List<CustomerSourceData> getCustomerSourceData() {
        // 資料庫中無相關資料進行統計 => 顯示假資料
        return Arrays.asList(
                new CustomerSourceData("Google廣告", 45),
                new CustomerSourceData("社交媒體", 30),
                new CustomerSourceData("推薦", 15),
                new CustomerSourceData("直接訪問", 10)
        );
    }

    @Override
    public List<RoomTypeSalesData> getRoomTypeSalesData() {
        List<RoomTypeSalesData> RoomTypeSalesDataList = Arrays.asList(
                new RoomTypeSalesData("單人房", 0),
                new RoomTypeSalesData("雙人房", 0),
                new RoomTypeSalesData("三人房", 0),
                new RoomTypeSalesData("家庭房", 0)
        );

        List<Object[]> roomTypeStats = orderDetailDao.findRoomTypeStats();

        for (Object[] stat : roomTypeStats) {
            Integer maxOccupancy = (Integer) stat[0]; // 最大入住人數
            Integer quantity = ((BigDecimal) stat[1]).intValue(); // 總數量

            // 根據 max_occupancy 確定房型並更新統計數據
            if (maxOccupancy == 1) {
                RoomTypeSalesData data = RoomTypeSalesDataList.get(0);
                data.setSales(data.getSales() + quantity);
            } else if (maxOccupancy == 2) {
                RoomTypeSalesData data = RoomTypeSalesDataList.get(1);
                data.setSales(data.getSales() + quantity);
            } else if (maxOccupancy == 3) {
                RoomTypeSalesData data = RoomTypeSalesDataList.get(2);
                data.setSales(data.getSales() + quantity);
            } else if (maxOccupancy >= 4) {
                RoomTypeSalesData data = RoomTypeSalesDataList.get(3);
                data.setSales(data.getSales() + quantity);
            }
        }

        return RoomTypeSalesDataList; // 返回結果
    }
}
