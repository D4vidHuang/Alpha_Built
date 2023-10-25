package com.ecadi.alphabuiltbackend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    public void deleteActionLogsByProjectId(int projectId);
}
