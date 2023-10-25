package com.ecadi.alphabuiltbackend.domain.account;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProjectIdAndUserIdPair {
    private int projectId;
    private int userId;

    public ProjectIdAndUserIdPair(int projectId, int userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
