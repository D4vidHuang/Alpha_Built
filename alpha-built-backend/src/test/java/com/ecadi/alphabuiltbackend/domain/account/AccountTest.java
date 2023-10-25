package com.ecadi.alphabuiltbackend.domain.account;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AccountTest {

    @Test
    void accountConstructorTest() {
        // Create an account
        Account account = new Account("testUser", "testPassword", "testEmail@test.com");

        // Check that the constructor and getters work correctly
        assertEquals("testUser", account.getUsername());
        assertEquals("testPassword", account.getPassword());
        assertEquals("testEmail@test.com", account.getEmail());
    }


}