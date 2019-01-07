package io.rosenwald.springDemo.rest;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * A JavaConfig configuration class specifying the properties used in the web security configuration. Currently used 
 * to provide the CSRF token as a cookie to the client of a REST endpoint. This allows keeping CSRF security enabled 
 * while still being able to test the endpoints with REST clients such as Insomnia or Postman. 
 * 
 * @author Nathaniel Rosenwald
 *
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
