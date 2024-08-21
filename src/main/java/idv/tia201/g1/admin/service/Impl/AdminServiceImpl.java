package idv.tia201.g1.admin.service.Impl;

import idv.tia201.g1.admin.dao.AdminDao;
import idv.tia201.g1.admin.service.AdminService;
import idv.tia201.g1.entity.Admin;
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
