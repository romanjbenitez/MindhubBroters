package com.mindhub.homebanking.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity

@Configuration
public class WebAuthorization extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/rest/**").hasAuthority("ADMIN")
                .antMatchers("/web/manager.html").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET,"/api/clients/current").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers(HttpMethod.GET,"/api/clients").hasAuthority("ADMIN")
                .antMatchers("/web/accounts.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/account.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/cards.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/transaction.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/create-cards.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/loan-application.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/web/settings.html").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers(HttpMethod.POST ,"/api/clients/current/**").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers(HttpMethod.POST ,"/api/transactions").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/api/loans").hasAnyAuthority("CLIENT","ADMIN")
                .antMatchers("/api/loans/admin").hasAuthority("ADMIN")
                .antMatchers( HttpMethod.POST,"/api/login").permitAll()
                .antMatchers( HttpMethod.POST,"/api/clients").permitAll();


        http.formLogin().usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        http.csrf().disable();

        http.headers().frameOptions().disable();

        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));



        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }

}
