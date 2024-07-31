package idv.tia201.g1.chat.controller;

import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.entity.User;
import idv.tia201.g1.utils.UserHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static idv.tia201.g1.utils.Constants.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @GetMapping("/rooms")
    public Result getRooms() {
        // 取得參予者

        UserAuth user = UserHolder.getUser();
        System.out.println(user);
        Integer id = null;
        switch (user.getRole()) {
            case ROLE_USER:
                id = UserHolder.getUser(User.class).getUserId();
                break;
            case ROLE_COMPANY:
                // TODO: 待組員完成後補充
                break;
            case ROLE_ADMIN:
                // TODO: 待組員完成後補充
                break;
        }
        return Result.ok(id);
    }
}
