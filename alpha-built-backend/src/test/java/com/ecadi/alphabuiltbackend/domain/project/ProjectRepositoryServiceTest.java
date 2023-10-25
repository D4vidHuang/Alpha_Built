package com.ecadi.alphabuiltbackend.domain.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


class ProjectRepositoryServiceTest {

    @InjectMocks
    ProjectRepositoryService projectRepositoryService;

    @Mock
    ProjectRepository projectRepository;

    ProjectSnapshot projectSnapshot;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        projectSnapshot = new ProjectSnapshot();
    }


    @Test
    void saveProject() {
        projectSnapshot.setProjectId(1);
        when(projectRepository.existsByProjectId(1)).thenReturn(false);

        assertDoesNotThrow(() -> projectRepositoryService.saveProject(projectSnapshot));

        when(projectRepository.existsByProjectId(1)).thenReturn(true);

        assertThrows(ProjectDatabaseException.ProjectExistInDatabaseException.class, () -> {
            projectRepositoryService.saveProject(projectSnapshot);
        });
    }

    @Test
    void updateProject() {
        when(projectRepository.existsByProjectId(1)).thenReturn(true);
        projectSnapshot.setProjectId(1);

        assertDoesNotThrow(() -> projectRepositoryService.updateProject(projectSnapshot));

        when(projectRepository.existsByProjectId(1)).thenReturn(false);

        assertThrows(ProjectDatabaseException.ProjectNotExistInDatabaseException.class, () -> {
            projectRepositoryService.updateProject(projectSnapshot);
        });
    }

    @Test
    void checkProjectExistByProjectId() {
        when(projectRepository.existsByProjectId(1)).thenReturn(true);

        assertTrue(projectRepositoryService.checkProjectExistByProjectId(1));

        when(projectRepository.existsByProjectId(1)).thenReturn(false);

        assertFalse(projectRepositoryService.checkProjectExistByProjectId(1));
    }

    @Test
    void clearDatabase() {
        doNothing().when(projectRepository).deleteAll();

        assertDoesNotThrow(() -> projectRepositoryService.clearDatabase());
    }
}