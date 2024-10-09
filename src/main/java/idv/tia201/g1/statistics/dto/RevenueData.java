package idv.tia201.g1.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueData {
    private Long currentRevenue; // 過去30天收入
    private Long revenueDifference; // 差額
}