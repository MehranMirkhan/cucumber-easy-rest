package io.github.mehranmirkhan.cucumber.rest.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsProvider {
    Class<? extends UserDetails> getUserDetailsClass();
}
