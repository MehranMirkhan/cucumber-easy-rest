package com.example.full.core.security;

import com.example.full.Params;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtUtils {
    private final Params params;

    public Optional<String> extractTokenFromHeader(HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            return Optional.of(bearerToken.substring(7));
        return Optional.empty();
    }

    public Claims getTokenClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(params.auth().jwtSecret())
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String generateTokenFromClaims(Claims claim) {
        Instant expire = Instant.now().plus(Duration.ofDays(1));
        return Jwts.builder()
                   .setClaims(claim)
                   .setExpiration(Date.from(expire))
                   .signWith(SignatureAlgorithm.HS256, params.auth().jwtSecret())
                   .compact();
    }

    public String generateToken(Principal principal) {
        Map<String, Object> map = new HashMap<>();
        map.put("roles", principal.getRoles());
        var claims = new DefaultClaims(map);
        claims.setSubject(principal.getUsername());
        return generateTokenFromClaims(claims);
    }

    public Principal parseToken(String token) {
        Claims claims = getTokenClaims(token);
        return Principal.builder()
                        .username(claims.getSubject())
                        .roles(Set.copyOf(claims.get("roles", Set.class)))
                        .build();
    }
}
