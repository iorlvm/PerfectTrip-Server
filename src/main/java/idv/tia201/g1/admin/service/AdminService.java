package idv.tia201.g1.admin.service;

import idv.tia201.g1.entity.Admin;

public interface AdminService {
    Admin login(String username, String password);
}
