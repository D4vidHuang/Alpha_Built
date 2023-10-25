package com.ecadi.alphabuiltbackend.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query("SELECT a FROM Account a WHERE a.username = ?1 OR a.email = ?1")
    Account findUserByUsernameOrEmail(String usernameOrEmail);

    @Query("SELECT COALESCE(MAX(a.id), 0) FROM Account a")
    Integer findMaxId();

    @Query("SELECT a FROM Account a WHERE a.id = ?1")
    Account findAccountById(int id);
}
