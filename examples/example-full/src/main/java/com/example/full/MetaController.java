package com.example.full;

import lombok.Builder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("public/meta")
public class MetaController {
    @GetMapping("health")
    Health getHealth(@RequestHeader(value = "lang", required = false) String lang,
                     @RequestHeader(value = "cookie", required = false) String cookie,
                     @RequestBody(required = false) Health.Request body) {
        return Health.builder().status(Health.Status.UP).lang(lang).cookie(cookie).body(body).build();
    }

    @Builder
    record Health(Status status, String lang, String cookie, Request body) {
        enum Status {UP, DOWN}

        record Request(String name, Gender gender) {
            enum Gender {MALE, FEMALE}
        }
    }
}
