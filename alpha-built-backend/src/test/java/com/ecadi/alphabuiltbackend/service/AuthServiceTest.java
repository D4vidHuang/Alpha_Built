package com.ecadi.alphabuiltbackend.service;

import com.ecadi.alphabuiltbackend.domain.account.Account;
import com.ecadi.alphabuiltbackend.domain.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadUserByUsernameSuccess() {
        String username = "testUser";
        Account account = new Account(username, "testPass", "test@test.com");

        when(accountRepository.findUserByUsernameOrEmail(username)).thenReturn(account);

        UserDetails userDetails = authService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals("testPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_user")));
    }

    @Test
    public void testLoadUserByUsernameUsernameNull() {
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(null);
        });

        assertEquals("用户名不能为空。\nUsername can't be empty.", exception.getMessage());
    }

    @Test
    public void testLoadUserByUsernameUserNotFound() {
        String username = "unknownUser";

        when(accountRepository.findUserByUsernameOrEmail(username)).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(username);
        });

        assertEquals("用户名或密码错误。\nWrong username or password.", exception.getMessage());
    }

    @Test
    public void testValidateAndRegisterSuccess() {
        String username = "newUser";
        String password = "newPass";
        String email = "new@new.com";

        when(accountRepository.findUserByUsernameOrEmail(username)).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenReturn(new Account());

        assertEquals(1, authService.validateAndRegister(username, password, email));
    }

    @Test
    public void testValidateAndRegisterUsernameExists() {
        String username = "existingUser";
        String password = "newPass";
        String email = "new@new.com";

        when(accountRepository.findUserByUsernameOrEmail(username)).thenReturn(new Account());

        assertEquals(0, authService.validateAndRegister(username, password, email));
    }

    @Test
    public void testValidateAndRegisterRegistrationFail() {
        String username = "newUserFail";
        String password = "newPass";
        String email = "new@new.com";

        when(accountRepository.findUserByUsernameOrEmail(username)).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenThrow(RuntimeException.class);

        assertEquals(-1, authService.validateAndRegister(username, password, email));
    }
}