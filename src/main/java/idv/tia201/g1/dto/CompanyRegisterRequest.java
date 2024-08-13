package idv.tia201.g1.dto;

import jakarta.validation.constraints.NotBlank;

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
    private CompanyRegisterRequest(){

    }

    public @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank String username) {
        this.username = username;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    public @NotBlank String getCompanyName() {return companyName;}

    public void setCompanyName(@NotBlank String companyName) {this.companyName = companyName;}

    public @NotBlank String getVatNumber() {return vatNumber;}

    public void setVatNumber(@NotBlank String vatNumber) {this.vatNumber = vatNumber;}

    public @NotBlank String getAddress() {return address;}

    public void setAddress(@NotBlank String address) {this.address = address;}

    public @NotBlank String getTelephone() {return telephone;}

    public void setTelephone(@NotBlank String telephone) {this.telephone = telephone;}
}
