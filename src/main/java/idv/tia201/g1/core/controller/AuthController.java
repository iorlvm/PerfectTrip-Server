package idv.tia201.g1.core.controller;

import idv.tia201.g1.core.service.TokenService;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.dto.AuthDTO;
import idv.tia201.g1.core.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private TokenService tokenService;
    
    @GetMapping("/auth-token")
    public Result validateTokenExpiry(@RequestHeader("Authorization") String authorizationHeader) {
        boolean isExpired = true;
        AuthDTO authDTO = new AuthDTO();

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            UserAuth userAuth = tokenService.validateToken(token);

            if (userAuth != null) {
                isExpired = false;
            }
            authDTO.setToken(token);
        }
        authDTO.setExpired(isExpired);

        return Result.ok(authDTO);
    }

    @GetMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenService.revokeToken(token);
        }
        return Result.ok();
    }
}
