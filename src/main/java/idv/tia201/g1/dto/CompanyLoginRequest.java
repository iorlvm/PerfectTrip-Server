package idv.tia201.g1.dto;

import jakarta.validation.constraints.NotBlank;

public class CompanyLoginRequest {
    @NotBlank  //NotBlank只用於string,不能為null且trim()之後size>0
    private String username;

    @NotBlank
    private String password;

    public CompanyLoginRequest() {

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

}
