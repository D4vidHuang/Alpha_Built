package com.ecadi.alphabuiltbackend.domain.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing operations on Projects in the database.
 * Handles interactions with the ProjectRepository.
 */
@Service
public class ProjectRepositoryService {

    Logger logger = LoggerFactory.getLogger("Project Database Logger");

    @Autowired
    ProjectRepository projectRepository;

    /**
     * Verifies that a loaded ProjectSnapshot is not null.
     * Throws an exception if the ProjectSnapshot is null.
     *
     * @param project The ProjectSnapshot object to check.
     * @throws ProjectDatabaseException.ProjectNotExistInDatabaseException
     *         If the ProjectSnapshot is null.
     */
    private void checkLoadedProjectSnapshotIsNotNull(ProjectSnapshot project) {
        if (project == null) {
            throw new ProjectDatabaseException.ProjectNotExistInDatabaseException();
        }
    }

    /**
     * Retrieves a ProjectSnapshot by its projectId from the database.
     * Throws an exception if the ProjectSnapshot is not found.
     *
     * @param projectId The ID of the ProjectSnapshot to retrieve.
     * @return The ProjectSnapshot with the specified projectId.
     * @throws ProjectDatabaseException.ProjectNotExistInDatabaseException
     *         If the ProjectSnapshot is not found in the database.
     */
    public ProjectSnapshot getProjectSnapshotByProjectIdStrict(int projectId) {
        ProjectSnapshot project = projectRepository.getProjectByProjectId(projectId);
        checkLoadedProjectSnapshotIsNotNull(project);
        return project;
    }

    /**
     * Retrieves a ProjectSnapshot by its projectId from the database.
     * Returns null if the ProjectSnapshot is not found.
     *
     * @param projectId The ID of the ProjectSnapshot to retrieve.
     * @return The ProjectSnapshot with the specified projectId, or null if it is not found.
     */
    public ProjectSnapshot getProjectSnapshotByProjectId(int projectId) {
        return projectRepository.getProjectByProjectId(projectId);
    }

    /**
     * Retrieves a Project by its projectId from the database.
     *
     * @param projectId The ID of the project to retrieve.
     * @return The Project with the specified projectId.
     * @throws ProjectDatabaseException.ProjectNotExistInDatabaseException If the Project does not exist.
     */
    public Project getProjectByProjectId(int projectId) {
        ProjectSnapshot projectSnapshot = getProjectSnapshotByProjectId(projectId);
        return projectSnapshot != null ? ProjectUtilService.constructProjectFromProjectSnapshot(projectSnapshot) : null;
    }

    /**
     * Saves a new Project to the database.
     *
     * @param project The Project to save.
     * @throws ProjectDatabaseException.ProjectExistInDatabaseException If a Project with the same projectId already exists.
     */
    void saveProject(ProjectSnapshot project) {
        int projectId = project.getProjectId();
        if (projectRepository.existsByProjectId(projectId)) {
            String errMessage = String.format("Project with project id %d has already existed in database.", projectId);
            logger.error(errMessage);
            throw new ProjectDatabaseException.ProjectExistInDatabaseException(errMessage);
        }
        projectRepository.save(project);
    }

    /**
     * Updates an existing Project in the database.
     *
     * @param project The Project to update.
     * @throws ProjectDatabaseException.ProjectNotExistInDatabaseException
     *      If the Project does not exist.
     */
    void updateProject(ProjectSnapshot project) {
        int projectId = project.getProjectId();
        if (!projectRepository.existsByProjectId(projectId)) {
            String errMessage = String.format("Update project with id %d does not exist in database.", projectId);
            logger.error(errMessage);
            throw new ProjectDatabaseException.ProjectNotExistInDatabaseException(errMessage);
        }
        projectRepository.save(project);
    }

    /**
     * Stores the state of a ProjectSnapshot in the database.
     * If a ProjectSnapshot with the same projectId already exists, it updates the existing ProjectSnapshot.
     * Otherwise, it saves the new ProjectSnapshot.
     *
     * @param projectSnapshot The ProjectSnapshot object whose state is to be stored.
     * @throws ProjectDatabaseException.ProjectExistInDatabaseException
     *         If an attempt is made to save a ProjectSnapshot that already exists in the database.
     * @throws ProjectDatabaseException.ProjectNotExistInDatabaseException
     *         If an attempt is made to update a ProjectSnapshot that does not exist in the database.
     */
    public void storeProjectState(ProjectSnapshot projectSnapshot) {
        int projectId = projectSnapshot.getProjectId();
        if (projectRepository.existsByProjectId(projectId)) {
            updateProject(projectSnapshot);
        } else {
            saveProject(projectSnapshot);
        }
    }


    /**
     * Checks if a Project exists by its projectId in the database.
     *
     * @param projectId The ID of the project to check.
     * @return true if a project with the specified projectId exists, false otherwise.
     */
    public boolean checkProjectExistByProjectId(int projectId) {
        return projectRepository.existsByProjectId(projectId);
    }

    /**
     * Clears all Projects from the database.
     */
    public void clearDatabase() {
        projectRepository.deleteAll();
    }

    /**
     * Retrieves all ProjectSnapshots from the database.
     *
     * @return A list of all ProjectSnapshots in the database.
     */
    public List<ProjectSnapshot> getAllProjects() {
        return projectRepository.findAll();
    }

}
