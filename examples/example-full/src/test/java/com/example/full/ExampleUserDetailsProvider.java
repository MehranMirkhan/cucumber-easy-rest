package com.example.full;

import com.example.full.core.security.Principal;
import io.github.mehranmirkhan.cucumber.rest.security.UserDetailsProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class ExampleUserDetailsProvider implements UserDetailsProvider {
    @Override
    public Class<? extends UserDetails> getUserDetailsClass() {
        return Principal.class;
    }
}
