package idv.tia201.g1.admin.dao;

import idv.tia201.g1.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDao extends JpaRepository<Admin, Integer> {
    Admin findByUsernameAndPassword(String username, String password);
}
