package idv.tia201.g1.qa.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.qa.entity.qa;
import idv.tia201.g1.qa.service.QaService;
import org.springframework.beans.factory.annotation.Autowired;
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
        List<qa> all = qaService.getAll();
        return Result.ok(all);
    }


}
