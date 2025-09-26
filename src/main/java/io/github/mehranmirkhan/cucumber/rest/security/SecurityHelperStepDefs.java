package io.github.mehranmirkhan.cucumber.rest.security;

import io.cucumber.java.en.Given;
import io.github.mehranmirkhan.cucumber.rest.HelpersManager;
import io.github.mehranmirkhan.cucumber.rest.core.ContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SecurityHelperStepDefs {
    private final HelpersManager helpersManager;
    private final ContextHelper  contextHelper;

    @Autowired(required = false)
    private UserDetailsProvider userDetailsProvider;

    @Given("^user:$")
    public void setupAuthenticationUser(List<Map<String, String>> body) {
        checkUserClassProviderDefined();
        UserDetails user = helpersManager.parseBody(body, userDetailsProvider.getUserDetailsClass()).get(0);
        contextHelper.setMockUser(user);
    }

    @Given("^user (.*) with role (.*)$")
    public void setupAuthenticationUser(String username, String role) {
        checkUserClassProviderDefined();
        setMockUser(username, role);
    }

    @Given("^admin$")
    public void setupAdmin() {
        checkUserClassProviderDefined();
        setMockUser("admin", "ROLE_ADMIN");
    }

    @Given("^anonymous$")
    public void setupAnonymous() {
        contextHelper.setMockUser(null);
    }

    protected void checkUserClassProviderDefined() {
        if (userDetailsProvider == null) {
            throw new RuntimeException("UserClassProvider is not defined. " +
                                       "Please consider creating a class implementing " +
                                       "the UserClassProvider interface.");
        }
    }

    @SneakyThrows
    protected void setMockUser(String username, String role) {
        List<Map<String, String>> body = List.of(Map.of("username", username,
                                                        "roles", "[\"" + role + "\"]"));
        setupAuthenticationUser(body);
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsProvider.class)
    public UserDetailsProvider userDetailsProvider() {
        return () -> UserDetails.class;
    }
}
