package idv.tia201.g1.member.service;

import idv.tia201.g1.member.entity.Admin;

public interface AdminService {
    Admin login(String username, String password);
}
