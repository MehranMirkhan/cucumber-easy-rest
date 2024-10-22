package com.example.full.core.auth;

import com.example.full.core.security.JwtUtils;
import com.example.full.core.security.Principal;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtUtils    jwtUtils;

    public SigninResponse authenticate(SigninRequest request) {
        User user = userService.getByUsername(request.username);
        Principal principal = Principal.builder()
                                       .username(user.getUsername())
                                       .password(user.getPassword())
                                       .roles(user.getRoles().stream()
                                                  .map(Role::getName)
                                                  .map(Enum::name)
                                                  .collect(Collectors.toSet()))
                                       .build();
        String token = jwtUtils.generateToken(principal);
        return SigninResponse.builder().token(token).build();
    }

    @Builder
    public record SigninRequest(String username, String password) {}

    @Builder
    public record SigninResponse(String token) {}
}
