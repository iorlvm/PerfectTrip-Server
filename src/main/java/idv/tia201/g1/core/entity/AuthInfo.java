package idv.tia201.g1.core.entity;

import lombok.Data;

@Data
public class AuthInfo implements UserAuth{
    private Integer id;
    private String role;
}
