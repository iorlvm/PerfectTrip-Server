package idv.tia201.g1.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyUpdateRequest {
    @NotNull//。@NotBlank 用於 String 類型，用於檢查該字串是否非空且不僅由空白字符組成。對於 Integer 類型，應使用 @NotNull。
    private  Integer companyId;

    @NotBlank
    private String username;

//    @NotBlank
//    private String password;

    @NotBlank
    private String companyName;

    @NotBlank
    private String vatNumber;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @NotBlank
    private  String telephone;

//    @NotNull
//    private Integer changeId;

    @NotNull
    private String manager;

    public CompanyUpdateRequest() {

    }

}


