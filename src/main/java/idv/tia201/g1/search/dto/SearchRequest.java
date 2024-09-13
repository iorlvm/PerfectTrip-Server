package idv.tia201.g1.search.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class SearchRequest {
    private Integer adultCount;
    private Integer childCount;
    private Date startDate;  // 開始日期
    private Date endDate;    // 結束日期
    private String destination;
    private Integer roomCount;

    private Integer page = 0;
    private Integer size = 20;
    private String orderBy;
    private Boolean isDesc = false;
}
