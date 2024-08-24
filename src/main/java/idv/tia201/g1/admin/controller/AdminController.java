package idv.tia201.g1.admin.controller;

import idv.tia201.g1.admin.service.AdminService;
import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.entity.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;
    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public RedirectView login(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              HttpSession session) {
        Admin login = adminService.login(username, password);
        if (login != null) {
            String token = tokenService.createToken(login);
            session.setAttribute("token", token);
            session.setAttribute("username", login.getUsername());
            session.setAttribute("role", login.getRole());
        }

        return new RedirectView("/");
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpSession session) {
        session.invalidate();
        return new RedirectView("/");
    }
}
