package idv.tia201.g1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyUpdateRequest {
    @NotBlank
    private  Integer companyId;

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

    @NotNull
    private Integer changeId;

}


