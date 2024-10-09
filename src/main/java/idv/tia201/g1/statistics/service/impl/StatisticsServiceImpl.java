package idv.tia201.g1.statistics.service.impl;

import idv.tia201.g1.member.constant.Gender;
import idv.tia201.g1.member.dao.UserDao;
import idv.tia201.g1.order.dao.OrderDao;
import idv.tia201.g1.order.dao.OrderDetailDao;
import idv.tia201.g1.statistics.dto.CustomerSourceData;
import idv.tia201.g1.statistics.dto.OrderStats;
import idv.tia201.g1.statistics.dto.RevenueData;
import idv.tia201.g1.statistics.dto.RoomTypeSalesData;
import idv.tia201.g1.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private UserDao userDao;

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
        List<Object[]> genderStatistics = userDao.findGenderStatistics();

        return genderStatistics.stream()
                .map(stat -> new CustomerSourceData(((Gender) stat[0]).name(), ((Long) stat[1]).intValue()))
                .toList();
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

    @Override
    public RevenueData getRevenueData() {
        LocalDate today = LocalDate.now();
        LocalDate startLast30Days = today.minusDays(30);
        LocalDate start30To60DaysAgo = today.minusDays(60);
        LocalDate end30To60DaysAgo = today.minusDays(31);

        // 將 LocalDate 轉換為 Timestamp
        Timestamp startLast30DaysTs = Timestamp.valueOf(startLast30Days.atStartOfDay());
        Timestamp todayTs = Timestamp.valueOf(today.atStartOfDay());
        Timestamp start30To60DaysAgoTs = Timestamp.valueOf(start30To60DaysAgo.atStartOfDay());
        Timestamp end30To60DaysAgoTs = Timestamp.valueOf(end30To60DaysAgo.atStartOfDay());

        // 查詢收入
        Long revenueLast30Days = orderDao.findRevenueBetweenDates(startLast30DaysTs, todayTs);
        Long revenue30To60Days = orderDao.findRevenueBetweenDates(start30To60DaysAgoTs, end30To60DaysAgoTs);

        // 處理可能為 null 的情況
        revenueLast30Days = (revenueLast30Days != null) ? revenueLast30Days : 0L;
        revenue30To60Days = (revenue30To60Days != null) ? revenue30To60Days : 0L;

        // 計算收入差額
        Long revenueDifference = revenueLast30Days - revenue30To60Days;

        return new RevenueData(revenueLast30Days, revenueDifference);
    }

    @Override
    public Map<String, Long> getNewCustomersStatistics() {
        Long newCustomersToday = userDao.countNewCustomersToday();
        Long newCustomersLast30Days = userDao.countNewCustomersInLast30Days(LocalDateTime.now().minusDays(30));

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalNewCustomers", newCustomersLast30Days);
        stats.put("newCustomersToday", newCustomersToday);
        return stats;
    }
}
