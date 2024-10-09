package idv.tia201.g1.core.controller;

import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.core.dto.Mail;
import idv.tia201.g1.core.utils.MailUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
public class MailController {
    @Value("${email.sender}")
    private String SENDER;
    @Value("${email.application.password}")
    private String APPLICATION_PASSWORD;
    
    @GetMapping("/test-mail")
    public Result testMail (@RequestParam String email) throws MessagingException {
        Mail mail = new Mail();
        mail.setRecipient(email);
        mail.setSubject("TEST");
        mail.setText("test");
        MailUtil.init(SENDER, APPLICATION_PASSWORD);
        MailUtil.sendMail(mail);
        return Result.ok();
    }
}
