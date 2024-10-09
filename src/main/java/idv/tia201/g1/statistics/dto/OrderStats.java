package idv.tia201.g1.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStats {
    private LocalDate orderDate;
    private Long orderCount;
    private BigDecimal totalRevenue;

    public OrderStats(Object orderDate, Object orderCount, Object totalRevenue) {
        this.orderDate = convertToLocalDate(orderDate);
        this.orderCount = (Long) orderCount;
        this.totalRevenue = (BigDecimal) totalRevenue;
    }

    private LocalDate convertToLocalDate(Object date) {
        if (date instanceof Date) {
            return ((Date) date).toLocalDate();
        }
        throw new IllegalArgumentException("Unsupported date type: " + date.getClass());
    }
}
