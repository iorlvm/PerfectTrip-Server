package idv.tia201.g1.admin.controller;

import idv.tia201.g1.utils.UserHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import static idv.tia201.g1.utils.Constants.ROLE_ADMIN;

@Controller
public class RouterController {
    @RequestMapping("/")
    public RedirectView home() {
        String role = UserHolder.getRole();

        if (!ROLE_ADMIN.equals(role)) {
            return new RedirectView("/login");
        } else {
            return new RedirectView("/dashboard");
        }
    }

    @RequestMapping("/login")
    public String login() {
        return "views/login";
    }


    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        // 管理中心主畫面
        model.addAttribute("content", "views/dashboard.jsp");
        return "layout";
    }
}
