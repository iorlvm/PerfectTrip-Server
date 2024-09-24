package idv.tia201.g1.order.dto;

import idv.tia201.g1.order.entity.OrderDetail;
import idv.tia201.g1.order.entity.OrderResidents;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
@Data
public class OrderDTO {

    private Integer companyId;
    private String hotelName;
    private  String hotelAddress;
    private  float hotelScore;
    private String city;
    private String photo;
    //TODO: 等待組員新增 <3<3
    private List<String> hotelFacilities;

    private Integer orderId;
    private Integer guestCount;
    private String payStatus;
    private Date startDate;
    private Date endDate;
    private List<String> productList;
    private Integer fullPrice;
    private Integer serviceFee;  //服務費
    private Integer discount; //折扣
    private Integer tax;   //稅金
    private Integer actualPrice;//實際金額
    private Timestamp createdDate;
    private String orderNotes;
    private String wishedTime;

    private List<OrderProductDTO> products;
    private List<OrderResidents> residents;
}
