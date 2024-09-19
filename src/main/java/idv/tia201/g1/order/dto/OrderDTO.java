package idv.tia201.g1.order.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.sql.Date;
import java.util.List;
@Data
public class OrderDTO {

    private Integer companyId;
    private String hotelName;
    private  String hotelAddress;
    private  float hotelScore;
    //TODO: 等待組員新增 <3<3
    private  List<String> hotelFacilities;

    private Integer orderId;
    private Date startDate;
    private Date endDate;
    private List<String> productList;
    private Integer fullPrice;
    private Integer serviceFee;  //服務費
    private Integer discount; //折扣
    private Integer tax;   //稅金
    private Integer actualPrice;//實際金額

}