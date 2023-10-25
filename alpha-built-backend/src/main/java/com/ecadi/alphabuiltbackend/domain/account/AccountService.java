package com.ecadi.alphabuiltbackend.domain.account;

import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class responsible for handling operations related to Accounts.
 * It encapsulates the logic needed to interact with Account data in the database.
 */
@Service
public class AccountService {

    /**
     * Account repository instance used for interacting with the database.
     */
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    /**
     * Constructs a new AccountService object with the specified AccountRepository and EntityManager instances.
     *
     * @param accountRepository the AccountRepository instance for interacting with Account data in the database
     * @param entityManager     the EntityManager instance for managing entity operations
     */
    public AccountService(AccountRepository accountRepository, EntityManager entityManager) {
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
    }

    /**
     * Fetches the next available User ID by finding the maximum existing ID and incrementing it.
     *
     * @return the next available User ID.
     */
    public Integer getNextUserId() {
        return accountRepository.findMaxId() + 1;
    }

    /**
     * Finds an Account based on either the username or email.
     * If no matching account is found, returns null.
     * If an account is found, returns an AccountInfo instance containing the ID, username, and email of the account.
     *
     * @param usernameOrEmail the username or email of the account to find.
     * @return an AccountInfo object if a matching account is found, or null if no match is found.
     */
    public AccountInfo findAccountInfoByUsernameOrEmail(String usernameOrEmail) {
        Account account = accountRepository.findUserByUsernameOrEmail(usernameOrEmail);
        if (account == null) {
            return null;
        }
        AccountInfo accountInfo = new AccountInfo(account.getId(), account.getUsername(), account.getEmail());
        accountInfo.getUserRolesInProjects().addAll(account.getUserRolesInProjects());
        return accountInfo;
    }

    /**
     * Finds an Account based on the account ID.
     *
     * @param id the ID of the account to find.
     * @return an Account object if a matching account is found, or null if no match is found.
     */
    public Account getAccountById(int id) {
        return accountRepository.findById(id).orElse(null);
    }

    /**
     * Updates the projects associated with an account.
     *
     * @param accountId  the ID of the account to update.
     * @param newProjects the list of new projects to assign to the account.
     * @throws RuntimeException if the account is not found with the specified ID.
     */
    @Transactional
    public void updateAccountProjects(int accountId, List<ProjectIdAndUserIdPair> newProjects) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("Account not found with id: " + accountId)
        );
        account.setUserRolesInProjects(newProjects);
        Account savedAccount = accountRepository.save(account);
        System.out.println("111");
    }

    /**
     * Finds an Account based on either the username or email.
     * If no matching account is found, returns null.
     * If an account is found, returns an AccountInfo instance containing the ID, username, and email of the account.
     *
     * @param usernameOrEmail the username or email of the account to find.
     * @return an AccountInfo object if a matching account is found, or null if no match is found.
     */
    public Account findAccountByUsernameOrEmail(String usernameOrEmail) {
        Account account = accountRepository.findUserByUsernameOrEmail(usernameOrEmail);
        if (account == null) {
            return null;
        }
        return account;
    }
}