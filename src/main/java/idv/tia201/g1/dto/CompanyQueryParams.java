package idv.tia201.g1.dto;

import lombok.Data;

@Data
public class CompanyQueryParams {
    private String orderBy;
    private String sort;
    private Integer limit;
    private Integer offset;
}
