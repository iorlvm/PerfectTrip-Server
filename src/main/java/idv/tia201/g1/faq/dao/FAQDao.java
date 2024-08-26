package idv.tia201.g1.faq.dao;

import idv.tia201.g1.faq.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQDao extends JpaRepository<FAQ, Integer> {

}
