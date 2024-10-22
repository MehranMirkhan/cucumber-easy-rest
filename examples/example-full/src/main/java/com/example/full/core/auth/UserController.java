package com.example.full.core.auth;

import com.example.full.core.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("authenticate")
    AuthService.SigninResponse authenticate(AuthService.SigninRequest request) {
        return authService.authenticate(request);
    }

    @GetMapping
    User.DTO get(@AuthenticationPrincipal Principal principal) {
        return User.DTO.Mapper.INSTANCE.toDto(userService.getByUsername(principal.getUsername()));
    }
}
