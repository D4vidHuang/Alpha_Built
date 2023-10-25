package com.ecadi.alphabuiltbackend.controller;

import com.ecadi.alphabuiltbackend.domain.account.auth.AccountInfo;
import com.ecadi.alphabuiltbackend.entity.RestBean;
import com.ecadi.alphabuiltbackend.model.Memory;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Resource
    private Memory memory;

    /**
     * Constructs a new ProjectController with the specified Memory instance.
     *
     * @param memory the Memory instance for managing project data
     */
    @Autowired
    public ProjectController(Memory memory) {
        this.memory = memory;
    }

    /**
     * Saves a project snapshot based on the provided project ID.
     *
     * @param accountInfo the AccountInfo object representing the user's account information
     * @param projectId   the ID of the project to save
     * @return a RestBean object containing the result of the save operation
     */
    @PostMapping("/save-project")
    public RestBean<String> saveProject(
            @SessionAttribute("account_info") AccountInfo accountInfo,
            @RequestParam("projectId") int projectId) {
        if (accountInfo.getUserRolesInProjects().stream().map(x -> x.getProjectId() == projectId).count() == 0) {
            return RestBean.failure(400, "您没有权限访问该项目。\n You don't have access to this project.");
        }
        try {
            memory.snapshotProject(projectId);
            return RestBean.success("保存Project - " + projectId + "成功。\n Project - "
                    + projectId + " saved successful.");
        } catch (Exception e) {
            return RestBean.failure(400, "保存Project - " + projectId + "失败。\n Project - "
                    + projectId + " failed to save.");
        }
    }

    /**
     * Removes a user from a project based on the provided user ID and project ID.
     *
     * @param userId    the ID of the user to remove from the project
     * @param projectId the ID of the project
     * @return a RestBean object containing the result of the removal operation
     */
    @PostMapping("/remove-user")
    public RestBean<String> removeUserFromProject(
            @RequestParam("userId") int userId,
            @RequestParam("projectId") int projectId) {
        if (memory.getProjectHandler(projectId).getActiveUserList()
                .stream()
                .filter(x -> x.getId() == userId)
                .count() == 0) {
            return RestBean.failure(400, "该用户不在该项目中。\n The user is not in this project.");
        }
        try {
            memory.getProjectHandler(projectId).removeActiveUser(userId);
            return RestBean.success();
        } catch (Exception e) {
            return RestBean.failure(400);
        }
    }
}
