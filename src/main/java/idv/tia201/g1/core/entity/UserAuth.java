package idv.tia201.g1.core.entity;

import idv.tia201.g1.member.entity.Admin;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.User;

import static idv.tia201.g1.core.utils.Constants.*;

// TODO: 請組員寫實體類時順便實作這個介面
public interface UserAuth {
    String getRole();

    default Integer getId(){
        String role = getRole();
        if (role == null) return null;

        switch (role) {
            case ROLE_USER:
                if (this instanceof User) {
                    return ((User) this).getUserId();
                }
                break;
            case ROLE_COMPANY:
                if (this instanceof Company) {
                    return ((Company) this).getCompanyId();
                }
                break;
            case ROLE_ADMIN:
                if (this instanceof Admin) {
                    return ((Admin) this).getAdminId();
                }
                break;
        }
        return null;
    }
}
