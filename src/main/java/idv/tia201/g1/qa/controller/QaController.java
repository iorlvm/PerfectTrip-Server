package idv.tia201.g1.qa.controller;

import idv.tia201.g1.dto.Result;
import idv.tia201.g1.entity.QuestionAnswer;
import idv.tia201.g1.qa.service.QaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QaController {

    //在spring自動注入Service
    @Autowired
    private QaService qaService;


    @GetMapping("/QAs")
    public Result Qa() {
        List<QuestionAnswer> all = qaService.getAll();
        return Result.ok(all);
    }


}
