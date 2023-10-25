package com.ecadi.alphabuiltbackend.domain.account.auth;

import com.ecadi.alphabuiltbackend.domain.account.ProjectIdAndUserIdPair;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountInfo {

    private int id;
    private String username;
    private String email;

    /**
     * The list of project IDs and user IDs pairs representing the user's roles in projects.
     */
    private List<ProjectIdAndUserIdPair> userRolesInProjects;

    /**
     * Constructs a new AccountInfo object with the specified ID, username, and email.
     * Initializes the userRolesInProjects list as an empty ArrayList.
     *
     * @param id       the ID of the account
     * @param username the username of the account
     * @param email    the email associated with the account
     */
    public AccountInfo(int  id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userRolesInProjects = new ArrayList<>();
    }
}
