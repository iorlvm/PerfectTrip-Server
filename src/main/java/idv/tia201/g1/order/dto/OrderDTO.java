package idv.tia201.g1.order.dto;

import java.sql.Date;
import java.util.List;

public class OrderDTO {


    private Integer couponId;

    private Date beginDate;

    private Date companyId;

    private Integer orderId;

    private List<Product> productList;

    public static class Product {
        private Integer productId;
        private Integer count;
    }
}
