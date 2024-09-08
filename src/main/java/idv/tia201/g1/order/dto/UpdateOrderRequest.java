package idv.tia201.g1.order.dto;

import lombok.Data;

@Data
public class UpdateOrderRequest {

    //姓名的姓氏欄位
    private String firstName;

    //姓名的名字欄位
    private String lastName;

    //e-mail欄位
    private String email;

    //國家欄位
    private String country ="Taiwan";

    //電話欄位
    private String phone;

    //備註欄位
    private String remark;
}
