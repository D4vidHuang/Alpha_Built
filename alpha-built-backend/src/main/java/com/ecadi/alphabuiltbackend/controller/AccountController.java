package com.ecadi.alphabuiltbackend.controller;

import com.ecadi.alphabuiltbackend.domain.account.Account;
import com.ecadi.alphabuiltbackend.domain.account.AccountService;
import com.ecadi.alphabuiltbackend.domain.account.ProjectIdAndUserIdPair;
import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.model.Memory;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @Resource
    private Memory memory;

    /**
     * Constructs a new AccountController with the specified AccountService instance.
     *
     * @param accountService the AccountService instance for handling account operations
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Logging in via Springframework work will result a SecurityContext object.
     * We can extract the account information from the SecurityContext object.
     * Retrieves the account information of the currently logged-in user.
     *
     * @param accountInfo the AccountInfo object retrieved from the session attribute
     * @return The account information of the currently logged in user.
     */
    @GetMapping("/me")
    public RestBean<AccountInfo> me(@SessionAttribute("account_info") AccountInfo accountInfo) {
        System.out.println(accountInfo);
        return RestBean.success(accountInfo);
    }

    /**
     * Retrieves the next available user ID.
     *
     * @return a RestBean object containing the next available user ID
     */
    @GetMapping("/next-id")
    public RestBean<Integer> getNextUserId() {
        return RestBean.success(accountService.getNextUserId());
    }

    /**
     * Creates a new project and associates it with the currently logged in user.
     *
     * @param accountInfo the AccountInfo object representing the user's account information
     * @return a RestBean object containing the ID of the newly created project
     */
    @GetMapping("/create-new-project")
    public RestBean<Integer> createNewProject(
            @SessionAttribute("account_info") AccountInfo accountInfo) {
        try {
            int nextProjectId = memory.getNextProjectId();
            memory.loadProject(nextProjectId);
            List<ProjectIdAndUserIdPair> newUserAndProjectsIdPairs = accountInfo.getUserRolesInProjects();
            newUserAndProjectsIdPairs.add(new ProjectIdAndUserIdPair(nextProjectId, accountInfo.getId()));
            accountService.updateAccountProjects(accountInfo.getId(), newUserAndProjectsIdPairs);
            return RestBean.success(nextProjectId);
        } catch (Exception e) {
            System.out.println("Failed to create new project.");
            return RestBean.failure(400);
        }
    }

    /**
     * Retrieves the projects associated with the currently logged in user.
     *
     * @param accountInfo the AccountInfo object representing the user's account information
     * @return a RestBean object containing the list of project ID and user ID pairs
     */
    @GetMapping("/user-projects")
    public RestBean<List<ProjectIdAndUserIdPair>> getUserProjects(
            @SessionAttribute("account_info") AccountInfo accountInfo) {
        return RestBean.success(accountService.getAccountById(accountInfo.getId()).getUserRolesInProjects());
    }

    /**
     * Adds a user to a project based on the provided username and project ID.
     *
     * @param username  the username of the user to add
     * @param projectId the ID of the project
     * @return a RestBean object containing the result of the addition operation
     */
    @PostMapping("/add-user-to-project")
    @Transactional
    public RestBean<String> addUserToProject(
            @RequestParam("username") String username,
            @RequestParam("projectId") int projectId) {
        Account account = accountService.findAccountByUsernameOrEmail(username);
        System.out.println(account);
        if (account == null) {
            return RestBean.failure(400, "该用户不存在。\n The user does not exist.");
        } else if (account.getUserRolesInProjects().stream().filter(x -> x.getProjectId() == projectId).count() > 0) {
            return RestBean.failure(400, "该用户已经在该项目中。\n The user is already in this project.");
        } else {
            try {
                int accountId = account.getId();
                List<ProjectIdAndUserIdPair> deepCopy = new ArrayList<>();

                for (ProjectIdAndUserIdPair pair : account.getUserRolesInProjects()) {
                    ProjectIdAndUserIdPair copyPair = new ProjectIdAndUserIdPair(pair.getProjectId(), pair.getUserId());
                    deepCopy.add(copyPair);
                }
                // Add the new project to the list
                deepCopy.add(new ProjectIdAndUserIdPair(projectId, accountId));

                accountService.updateAccountProjects(accountId, deepCopy);
                return RestBean.success("添加用户成功。\n User added successful.");
            } catch (Exception e) {
                return RestBean.failure(400, "添加用户失败。\n Failed to add user.");
            }
        }
    }
}
