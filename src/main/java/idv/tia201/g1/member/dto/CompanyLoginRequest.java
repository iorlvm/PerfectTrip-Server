package idv.tia201.g1.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
@Data
@Validated
public class CompanyLoginRequest {
    @NotBlank  //NotBlank只用於string,不能為null且trim()之後size>0
    private String username;

    @NotBlank
    private String password;

}
