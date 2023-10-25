package com.ecadi.alphabuiltbackend.domain.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;


@DataJpaTest
public class ProjectRepositoryTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void testGetProjectByProjectId() {
        // Create a ProjectSnapshot and persist it using the TestEntityManager
        ProjectSnapshot projectSnapshot = new ProjectSnapshot(1, 2, new ArrayList<>());
        entityManager.merge(projectSnapshot);
        entityManager.flush();


        // Invoke the getProjectByProjectId() method on the ProjectRepository
        ProjectSnapshot foundProject = projectRepository.getProjectByProjectId(1);

        // Perform assertions to verify the result
        Assertions.assertNotNull(foundProject);
        Assertions.assertEquals(projectSnapshot.getProjectId(), foundProject.getProjectId());
    }


    @Test
    public void testExistsByProjectId() {
        // Create a ProjectSnapshot and persist it using the TestEntityManager
        ProjectSnapshot projectSnapshot = new ProjectSnapshot(1, 2, new ArrayList<>());
        ProjectSnapshot mergedProject = entityManager.merge(projectSnapshot);
        entityManager.flush();

        // Invoke the existsByProjectId() method on the ProjectRepository
        boolean exists = projectRepository.existsByProjectId(mergedProject.getProjectId());

        // Perform assertions to verify the result
        Assertions.assertTrue(exists);
    }
}
