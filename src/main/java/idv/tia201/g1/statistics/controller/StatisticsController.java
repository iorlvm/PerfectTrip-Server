package idv.tia201.g1.statistics.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.statistics.dto.CustomerSourceData;
import idv.tia201.g1.statistics.dto.OrderStats;
import idv.tia201.g1.statistics.dto.RoomTypeSalesData;
import idv.tia201.g1.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;


    @GetMapping("/daily-orders")
    public Result getDailyOrders() {
        List<OrderStats> dailyOrderData = statisticsService.getDailyOrderData();

        return Result.ok(dailyOrderData, 7L);
    }

    @GetMapping("/customer-source")
    public List<CustomerSourceData> getCustomerSource() {
        return statisticsService.getCustomerSourceData();
    }

    @GetMapping("/room-type-sales")
    public Result getRoomTypeSales() {
        List<RoomTypeSalesData> roomTypeSalesData = statisticsService.getRoomTypeSalesData();
        return Result.ok(roomTypeSalesData);
    }
}
