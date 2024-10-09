package idv.tia201.g1.statistics.service;

import idv.tia201.g1.statistics.dto.CustomerSourceData;
import idv.tia201.g1.statistics.dto.OrderStats;
import idv.tia201.g1.statistics.dto.RoomTypeSalesData;

import java.util.List;

public interface StatisticsService {
    List<OrderStats> getDailyOrderData();
    List<CustomerSourceData> getCustomerSourceData();
    List<RoomTypeSalesData> getRoomTypeSalesData();
}
