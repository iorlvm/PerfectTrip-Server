package idv.tia201.g1.faq.service.impl;

import idv.tia201.g1.faq.entity.FAQ;
import idv.tia201.g1.faq.dao.FAQDao;
import idv.tia201.g1.faq.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FAQServiceImpl implements FAQService {
     @Autowired
     private FAQDao FAQDao;

    @Override
    public List<FAQ> getAll() {
        List<FAQ> all = FAQDao.findAll();
        return all;
    }
}
