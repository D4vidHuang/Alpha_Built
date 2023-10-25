package com.ecadi.alphabuiltbackend.domain.account;

import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    AccountService accountService;

    AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository, mock(EntityManager.class));
    }

    @Test
    void getNextUserId() {
        when(accountRepository.findMaxId()).thenReturn(10);
        var result = accountService.getNextUserId();
        assertEquals(11, result);
    }

    @Test
    void findAccountInfoByUsernameOrEmail_null() {
        String username = "anon";
        when(accountRepository.findUserByUsernameOrEmail(username))
                .thenReturn(null);

        assertNull(accountService.findAccountInfoByUsernameOrEmail(username));
    }

    @Test
    void findAccountInfoByUsernameOrEmail_success() {
        String username = "anon";
        Account account = new Account("anon", "123412", "abc@gmail.com");
        when(accountRepository.findUserByUsernameOrEmail(username))
                .thenReturn(account);

        AccountInfo accountInfo = accountService.findAccountInfoByUsernameOrEmail(username);
        assertEquals("anon", accountInfo.getUsername());
        assertEquals("abc@gmail.com", accountInfo.getEmail());
    }

    @Test
    void getAccountById() {
        Account account = new Account("anon", "123412", "abc@gmail.com");
        when(accountRepository.findById(10)).thenReturn(Optional.of(account));
        Account result = accountService.getAccountById(10);
        assertEquals(account, result);
    }

    @Test
    void updateAccountProjects() {
        ProjectIdAndUserIdPair pair = new ProjectIdAndUserIdPair(20, 10);
        List newProjects = List.of(pair);
        Account account = new Account("anon", "123412", "abc@gmail.com");
        when(accountRepository.findById(5)).thenReturn(Optional.of(account));

        accountService.updateAccountProjects(5, newProjects);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void findAccountByUsernameOrEmail_noSuchUser() {
        when(accountRepository.findUserByUsernameOrEmail("anon")).thenReturn(null);
        Account result = accountService.findAccountByUsernameOrEmail("anon");
        assertNull(result);
    }

    @Test
    void findAccountByUsernameOrEmail_success() {
        Account account = new Account("anon", "123412", "abc@gmail.com");
        when(accountRepository.findUserByUsernameOrEmail("anon")).thenReturn(account);
        Account result = accountService.findAccountByUsernameOrEmail("anon");
        assertEquals(account, result);
    }
}