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
        return "/views/login";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        // 管理中心主畫面
        model.addAttribute("content", "views/dashboard.jsp");
        model.addAttribute("pageTitle", "報告總覽");
        return "/layout";
    }

    @RequestMapping("/messages")
    public String messages(Model model) {
        // 訊息中心主畫面
        model.addAttribute("content", "views/chat.jsp");
        model.addAttribute("pageTitle", "訊息中心");
        return "/layout";
    }

    @RequestMapping("/settings")
    public String settings(Model model) {
        // 設定
        model.addAttribute("content", "views/settings.jsp");
        model.addAttribute("pageTitle", "設定");
        return "/layout";
    }

    @RequestMapping("/reservations/list")
    public String currentReservations(Model model) {
        // 當前訂單
        model.addAttribute("content", "views/reservations/list.jsp");
        model.addAttribute("pageTitle", "訂單列表");
        return "/layout";
    }

    @RequestMapping("/reservations/disputes")
    public String disputesReservations(Model model) {
        // 歷史訂單
        model.addAttribute("content", "views/reservations/disputes.jsp");
        model.addAttribute("pageTitle", "爭議處理");
        return "/layout";
    }

    @RequestMapping("/customers/list")
    public String customersList(Model model) {
        // 顧客列表
        model.addAttribute("content", "views/customers/list.jsp");
        model.addAttribute("pageTitle", "客戶列表");
        return "/layout";
    }

    @RequestMapping("/customers/feedback")
    public String customersFeedback(Model model) {
        // 客戶反饋
        model.addAttribute("content", "views/customers/feedback.jsp");
        model.addAttribute("pageTitle", "客戶反饋");
        return "/layout";
    }

    @RequestMapping("/companies/list")
    public String companiesList(Model model) {
        // 商家列表
        model.addAttribute("content", "views/companies/list.jsp");
        model.addAttribute("pageTitle", "商家列表");
        return "/layout";
    }

    @RequestMapping("/companies/verification")
    public String companyVerification(Model model) {
        // 商家審核
        model.addAttribute("content", "views/companies/verification.jsp");
        model.addAttribute("pageTitle", "商家驗證");
        return "/layout";
    }


}
