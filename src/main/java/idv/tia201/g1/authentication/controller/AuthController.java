package idv.tia201.g1.authentication.controller;

import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.dto.AuthDTO;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
