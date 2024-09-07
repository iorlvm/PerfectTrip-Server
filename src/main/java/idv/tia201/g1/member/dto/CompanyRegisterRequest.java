package idv.tia201.g1.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String companyName;

    @NotBlank
    private String vatNumber;

    @NotBlank
    private String address;

    @NotBlank
    private  String telephone;

    @NotBlank
    private String manager;


}
