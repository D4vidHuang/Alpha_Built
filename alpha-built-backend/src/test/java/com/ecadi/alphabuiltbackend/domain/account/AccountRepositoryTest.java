package com.ecadi.alphabuiltbackend.domain.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testFindUserByUsernameOrEmail() {
        // Create a new account and persist it
        Account account = new Account("testUser", "testPassword", "testEmail@test.com");
        entityManager.persist(account);
        entityManager.flush();

        // Use the repository to fetch the user by username
        Account foundByUserName = accountRepository.findUserByUsernameOrEmail("testUser");

        // Use the repository to fetch the user by email
        Account foundByEmail = accountRepository.findUserByUsernameOrEmail("testEmail@test.com");

        // Assert that the found accounts are the same as the original account
        assertThat(foundByUserName).isEqualTo(account);
        assertThat(foundByEmail).isEqualTo(account);
    }
}
