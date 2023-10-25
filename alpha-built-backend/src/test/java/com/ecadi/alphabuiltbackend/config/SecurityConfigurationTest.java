package com.ecadi.alphabuiltbackend.config;

import com.alibaba.fastjson.JSONObject;
import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    @InjectMocks
    SecurityConfiguration securityConfiguration;

    @Mock
    AuthService authService;


    @Test
    void onAuthenticationSuccess_login() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        PrintWriter pw = mock(PrintWriter.class);
        when(request.getRequestURI()).thenReturn("/login");
        when(response.getWriter()).thenReturn(pw);
        securityConfiguration.onAuthenticationSuccess(request, response, mock(Authentication.class));

        verify(pw, times(1))
                .write(JSONObject.toJSONString(RestBean.success("登录成功\nSuccessful logged in!")));
    }

    @Test
    void onAuthenticationSuccess_logout() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        PrintWriter pw = mock(PrintWriter.class);
        when(request.getRequestURI()).thenReturn("/logout");
        when(response.getWriter()).thenReturn(pw);
        securityConfiguration.onAuthenticationSuccess(request, response, mock(Authentication.class));

        verify(pw, times(1))
                .write(JSONObject.toJSONString(RestBean.success("退出登录成功\nSuccessful logged out!")));
    }

    @Test
    void onAuthenticationFailure() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authenticationException = new AccountExpiredException("expired");
        PrintWriter pw = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(pw);
        securityConfiguration.onAuthenticationFailure(request, response, authenticationException);
        verify(pw, times(1))
                .write(JSONObject.toJSONString(RestBean.failure(401, authenticationException.getMessage())));
    }
}