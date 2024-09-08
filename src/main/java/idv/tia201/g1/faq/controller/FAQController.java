package idv.tia201.g1.faq.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.faq.entity.FAQ;
import idv.tia201.g1.faq.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FAQController {

    //在spring自動注入Service
    @Autowired
    private FAQService FAQService;


    @GetMapping("/api/faq")
    public Result faq() {
        List<FAQ> all = FAQService.getAll();
        return Result.ok(all);
    }


}
