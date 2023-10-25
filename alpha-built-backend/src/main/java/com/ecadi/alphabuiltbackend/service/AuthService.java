package com.ecadi.alphabuiltbackend.service;

import com.ecadi.alphabuiltbackend.domain.account.Account;
import com.ecadi.alphabuiltbackend.domain.account.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    /**
     * Locates the user based on the username and email. In the actual implementation, the search
     * may be case-sensitive, or case-insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested.
     *
     * @param username the username or email identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空。\nUsername can't be empty.");
        }
        Account account = accountRepository.findUserByUsernameOrEmail(username);
        if (account == null) {
            // Ambiguous message to protect malicious behaviour
            throw new UsernameNotFoundException("用户名或密码错误。\nWrong username or password.");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    /**
     * Validates and registers a new user.
     *
     * @param username The username of the user to be registered.
     * @param password The password of the user to be registered.
     * @param email The email of the user to be registered.
     * @return 0 if the username or email already exists, -1 if the registration fails, 1 if the registration succeeds.
     */
    public int validateAndRegister(String username, String password, String email) {
        Object a = accountRepository.findUserByUsernameOrEmail(username);
        int aa = 0;
        if (accountRepository.findUserByUsernameOrEmail(username) != null) {
            return 0;
        } else {
            Account account = new Account(username, password, email);
            try {
                Account savedAccount = accountRepository.save(account);
                return 1;
            } catch (Exception e) {
                return -1;
            }
        }
    }


}
