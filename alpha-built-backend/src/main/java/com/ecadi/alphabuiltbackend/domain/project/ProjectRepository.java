package com.ecadi.alphabuiltbackend.domain.project;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface for managing Project persistence.
 * Extends Spring's JpaRepository to provide CRUD operations.
 */
public interface ProjectRepository extends JpaRepository<ProjectSnapshot, Long> {

    /**
     * Retrieves a Project by its projectId.
     *
     * @param projectId The ID of the project to retrieve.
     * @return The Project with the specified projectId, or null if no such project exists.
     */
    public ProjectSnapshot getProjectByProjectId(int projectId);

    /**
     * Checks if a Project exists by its projectId.
     *
     * @param projectId The ID of the project to check.
     * @return true if a project with the specified projectId exists, false otherwise.
     */
    public boolean existsByProjectId(int projectId);
}