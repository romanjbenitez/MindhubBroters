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
                .antMatchers("/rest/**").hasAuthority("CLIENT")
                .antMatchers("/web/manager.html").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET,"/api/**").hasAuthority("CLIENT")
                .antMatchers("/web/accounts.html").hasAuthority("CLIENT")
                .antMatchers("/web/account.html").hasAuthority("CLIENT")
                .antMatchers("/web/cards.html").hasAuthority("CLIENT")
                .antMatchers("/web/transaction.html").hasAuthority("CLIENT")
                .antMatchers("/web/create-cards.html").hasAuthority("CLIENT")
                .antMatchers("/web/loan-application.html").hasAuthority("CLIENT")
                .antMatchers("/web/settings.html").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST ,"/api/clients/current/**").hasAuthority("CLIENT")
                .antMatchers(HttpMethod.POST ,"/api/transactions").hasAuthority("CLIENT")
                .antMatchers("/api/loans").hasAuthority("CLIENT")
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
