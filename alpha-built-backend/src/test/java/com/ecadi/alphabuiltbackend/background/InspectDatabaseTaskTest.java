package com.ecadi.alphabuiltbackend.background;

import com.ecadi.alphabuiltbackend.domain.project.ProjectSnapshot;
import com.ecadi.alphabuiltbackend.domain.user.ActionLog;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.ecadi.alphabuiltbackend.model.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

class InspectDatabaseTaskTest {
    @InjectMocks
    private InspectDatabaseTask inspectDatabaseTask;

    @Mock
    private Memory memory;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void performTaskTest() {
        // Set up data
        ProjectSnapshot projectSnapshot = mock(ProjectSnapshot.class);
        User user = mock(User.class);
        ActionLog actionLog = mock(ActionLog.class);
        List<ProjectSnapshot> projectSnapshots = Arrays.asList(projectSnapshot);
        List<User> users = Arrays.asList(user);
        List<ActionLog> actionLogs = Arrays.asList(actionLog);

        // Set up mocks to return data
        Mockito.when(memory.loadAllProjectSnapsFromDatabase()).thenReturn(projectSnapshots);
        Mockito.when(memory.loadAllUsersInDatabaseForLog()).thenReturn(users);
        Mockito.when(user.getActionLogList()).thenReturn(actionLogs);

        // Run method under test
        inspectDatabaseTask.performTask();

        // Verify interactions
        Mockito.verify(memory).loadAllProjectSnapsFromDatabase();
        Mockito.verify(memory).loadAllUsersInDatabaseForLog();
        Mockito.verify(user).getActionLogList();
    }
}