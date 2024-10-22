package com.example.full;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.example")
public record Params(Auth auth) {
    public record Auth(String defaultAdminUsername, String defaultAdminPassword, String jwtSecret) {}
}
