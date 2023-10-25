package com.ecadi.alphabuiltbackend.config;

import com.alibaba.fastjson.JSONObject;

import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.service.AuthService;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * This is the security configuration class for the application.
 * It uses Spring Security to handle web security concerns such as authentication and authorization.
 * It is annotated with @EnableWebSecurity to import Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * The AuthService object is automatically injected by Spring.
     */
    @Resource
    AuthService authService;

    /**
     * This method defines the custom security filter chain for the application.
     * It requires authentication for any request, configures form login, logout, and exception handling.
     *
     * @param http the HttpSecurity object
     * @return the SecurityFilterChain object
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                    .authorizeHttpRequests()
                    .requestMatchers(request -> request.getServletPath().startsWith("/api/auth/"))
                    .permitAll()
                    .requestMatchers(request -> request.getServletPath().startsWith("/h2-console"))
                    .permitAll()
                    .anyRequest().authenticated()
                .and()
                    .headers().frameOptions().disable()
                .and()
                    .formLogin()
                    .loginProcessingUrl("/api/auth/login")
                    .successHandler(this::onAuthenticationSuccess)
                    .failureHandler(this::onAuthenticationFailure)
                .and()
                    .logout().logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler(this::onAuthenticationSuccess)
                .and()
                    .csrf().disable().cors().configurationSource(this.corsConfigurationSource())
                .and()
                .build();
    }


    // TODO: The current cors configuration is example, for develop convince all cross-origin is allowed.
    // TODO: We need a server ip of the front-end to set the allowed origin.
    /**
     * This method configures the CORS policy for the application.
     * It allows all origins, headers, methods, and credentials.
     * Allowing all  cross-origin requests is extremely malicious.
     *
     * @return the CorsConfigurationSource object
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*");
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }


    /**
     * This method creates an AuthenticationManager using the AuthService for user detail service.
     *
     * @param httpSecurity the HttpSecurity object
     * @return the AuthenticationManager object
     * @throws Exception if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authService)
                .and()
                .build();
    }

    /**
     * This method provides a BCryptPasswordEncoder for password encoding.
     *
     * @return a BCryptPasswordEncoder object
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This method handles successful authentication events.
     * It modifies the HTTP response to return a success message in JSON format.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param authentication the Authentication object
     * @throws IOException if an input or output exception occurred
     * @throws ServletException if a servlet exception occurs
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        if (request.getRequestURI().endsWith("/login")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功\nSuccessful logged in!")));
        } else if (request.getRequestURI().endsWith("/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("退出登录成功\nSuccessful logged out!")));
        }
    }

    /**
     * This method handles failed authentication events.
     * It modifies the HTTP response to return a failure message in JSON format.
     *
     * @param request the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param exception the AuthenticationException object
     * @throws IOException if an input or output exception occurred
     * @throws ServletException if a servlet exception occurs
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }

}
