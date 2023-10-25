package com.ecadi.alphabuiltbackend.interceptor;

import com.ecadi.alphabuiltbackend.domain.account.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
class AuthorizeInterceptorTest {

    @InjectMocks
    AuthorizeInterceptor authorizeInterceptor;

    @Mock
    AccountService accountService;

    @WithMockUser("anon")
    @Test
    void preHandle() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = new Object();

        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        boolean result = authorizeInterceptor.preHandle(request, response, handler);

        assertTrue(result);
        verify(accountService, times(1)).findAccountInfoByUsernameOrEmail("anon");
    }
}