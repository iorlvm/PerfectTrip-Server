package idv.tia201.g1.member.dao;

import idv.tia201.g1.member.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDao extends JpaRepository<Admin, Integer> {
    Admin findByUsernameAndPassword(String username, String password);
}
