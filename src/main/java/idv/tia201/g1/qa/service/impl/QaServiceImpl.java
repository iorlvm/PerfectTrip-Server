package idv.tia201.g1.qa.service.impl;

import idv.tia201.g1.entity.QuestionAnswer;
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
    public List<QuestionAnswer> getAll() {
        List<QuestionAnswer> all = qaDao.findAll();
        return all;
    }
}
