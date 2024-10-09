package idv.tia201.g1.statistics.service;

import idv.tia201.g1.statistics.dto.CustomerSourceData;
import idv.tia201.g1.statistics.dto.OrderStats;
import idv.tia201.g1.statistics.dto.RevenueData;
import idv.tia201.g1.statistics.dto.RoomTypeSalesData;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<OrderStats> getDailyOrderData();
    List<CustomerSourceData> getCustomerSourceData();
    List<RoomTypeSalesData> getRoomTypeSalesData();
    RevenueData getRevenueData();

    Map<String, Long> getNewCustomersStatistics();
}
