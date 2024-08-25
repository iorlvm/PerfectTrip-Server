package idv.tia201.g1.qa.service.impl;

import idv.tia201.g1.qa.entity.qa;
import idv.tia201.g1.qa.dao.QaDao;
import idv.tia201.g1.qa.service.QaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaServiceImpl implements QaService {
     @Autowired
     private QaDao qaDao;

    @Override
    public List<qa> getAll() {
        List<qa> all = qaDao.findAll();
        return all;
    }
}
