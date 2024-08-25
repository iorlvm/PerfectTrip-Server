package idv.tia201.g1.member.service.Impl;

import idv.tia201.g1.member.dao.AdminDao;
import idv.tia201.g1.member.service.AdminService;
import idv.tia201.g1.member.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDao adminDao;

    @Override
    public Admin login(String username, String password) {
        return adminDao.findByUsernameAndPassword(username, password);
    }
}
