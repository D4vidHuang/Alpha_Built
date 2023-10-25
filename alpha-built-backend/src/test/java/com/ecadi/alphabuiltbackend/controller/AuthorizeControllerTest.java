package com.ecadi.alphabuiltbackend.controller;

import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.service.AuthService;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizeControllerTest {

    @InjectMocks
    private AuthorizeController controller;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    private AuthService authService;

    @Mock
    private BCryptPasswordEncoder encoder;

    private MockMvc mockMvc;

    /**
     * Mock the AuthService.validateAndRegister() method.
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(encoder.encode(anyString())).thenReturn("encodedPassword");

        when(authService.validateAndRegister(anyString(), anyString(), anyString())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String username = (String) args[0];

                switch (username) {
                    case "usernameExists":
                        return 0;
                    case "usernameFailure":
                        return -1;
                    case "usernameSuccess":
                        return 1;
                    case "usernameError":
                        return 2;
                    default:
                        return 0;
                }
            }
        });
    }

    @Test
    public void signUpUser_alreadyExists() {
        RestBean<String> response = controller.signUpUser("usernameExists", "password", "email");
        assertEquals(400, response.getCode());
        assertEquals("用户名或邮箱已存在。\nUsername or email already exists.", response.getMessage());
    }

    @Test
    public void signUpUser_failure() {
        RestBean<String> response = controller.signUpUser("usernameFailure", "password", "email");
        assertEquals(400, response.getCode());
        assertEquals("注册失败。\nSign up failed.", response.getMessage());
    }

    @Test
    public void signUpUser_success() {
        RestBean<String> response = controller.signUpUser("usernameSuccess", "password", "email");
        assertEquals(200, response.getCode());
        assertEquals("注册成功。\nSign up successful.", response.getMessage());
    }

    @Test
    public void signUpUser_unknownError() {
        RestBean<String> response = controller.signUpUser("usernameError", "password", "email");
        assertEquals(400, response.getCode());
        assertEquals("未知错误。\nUnknown error.", response.getMessage());
    }

    @Test
    public void testValidUsername() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .param("username", "validusername")
                        .param("password", "password123")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testValidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/sign-up")
                        .param("username", "validusername")
                        .param("password", "password123")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void testInvalidUsernamePattern() throws Exception {
        // invalid characters
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/auth/sign-up")
                    .param("username", "invalid_username_with_special_chars!")
                    .param("password", "password123")
                    .param("email", "test@example.com"));
        });
        // 3 characters
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/auth/sign-up")
                    .param("username", "admi")
                    .param("password", "password123")
                    .param("email", "test@example.com"));
        });
        // 20 characters
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/auth/sign-up")
                    .param("username", "adminadminadminadmin")
                    .param("password", "password123")
                    .param("email", "test@example.com"));
        });
    }

    @Test
    public void testInvalidEmailPattern() throws Exception {
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/auth/sign-up")
                    .param("username", "validusername")
                    .param("password", "password123")
                    .param("email", "invalidEmail"));
        });
    }

    @Test
    public void testInvalidPasswordLength() throws Exception {
        // valid characters
        mockMvc.perform(post("/api/auth/sign-up")
                .param("username", "admin")
                .param("password", "password123")
                .param("email", "test@example.com"))
                .andExpect(status().isOk());
        // 5 characters
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/auth/sign-up")
                    .param("username", "adminin")
                    .param("password", "passw")
                    .param("email", "test@example.com"));
        });
        // 20 characters
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/api/auth/sign-up")
                    .param("username", "admin")
                    .param("password", "adminadminadminadmin")
                    .param("email", "test@example.com"));
        });
    }
}
