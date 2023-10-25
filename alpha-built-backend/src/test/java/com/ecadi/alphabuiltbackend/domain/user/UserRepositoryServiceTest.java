package com.ecadi.alphabuiltbackend.domain.user;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRepositoryServiceTest {

    @InjectMocks
    private UserRepositoryService userRepositoryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActionLogRepository actionLogRepository;

    @Mock
    private ChannelHandlerContext channelHandlerContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserByUserId() {
        User testUser = new User(1, 1, channelHandlerContext);
        when(userRepository.getUserByUserId(1)).thenReturn(testUser);
        User retrievedUser = userRepositoryService.getUserByUserId(1);
        assertEquals(testUser.getUserId(), retrievedUser.getUserId());
    }

    @Test
    public void testGetUserByUserIdNotFound() {
        when(userRepository.getUserByUserId(1)).thenReturn(null);
        assertThrows(UserDatabaseException.UserNotExistInDatabaseException.class, () -> {
            userRepositoryService.getUserByUserId(1);
        });
    }

    @Test
    void checkUserExistingInDatabase() {
        int userId = 1;
        User user = new User(1, 1, channelHandlerContext);
        when(userRepository.getUserByUserId(userId)).thenReturn(user);

        boolean exists = userRepositoryService.checkUserExistingInDatabase(userId);

        assertTrue(exists);
        verify(userRepository, times(1)).getUserByUserId(userId);
    }

    @Test
    void checkUserNotExistingInDatabase() {
        int userId = 2;
        when(userRepository.getUserByUserId(userId)).thenReturn(null);

        boolean exists = userRepositoryService.checkUserExistingInDatabase(userId);

        assertFalse(exists);
        verify(userRepository, times(1)).getUserByUserId(userId);
    }

    @Test
    void clearDatabase() {
        userRepositoryService.clearDatabase();
        verify(userRepository, times(1)).deleteAll();
        verify(actionLogRepository, times(1)).deleteAll();
    }

    @Test
    void addNewUserToDatabaseTest() {
        User user = new User(1, 1, channelHandlerContext);
        userRepositoryService.saveUser(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveActionLogTest() {
        ObjectNode properties = JsonNodeFactory.instance.objectNode();
        properties.put("key", "value");

        ActionLog actionLog1
                = new ActionLog(1, 2, 200, 3, MeshAction.CREATE, properties);
        ActionLog actionLog2
                = new ActionLog(1, 2, 100, 3, MeshAction.CREATE, properties);
        userRepositoryService.saveActionLog(actionLog1);
        verify(actionLogRepository, times(1)).save(actionLog1);
    }

    @Test
    void saveActionLogsTest() {
        ObjectNode properties = JsonNodeFactory.instance.objectNode();
        properties.put("key", "value");

        ActionLog actionLog1
                = new ActionLog(1, 2, 200, 3, MeshAction.CREATE, properties);
        ActionLog actionLog2
                = new ActionLog(1, 2, 100, 3, MeshAction.CREATE, properties);
        ArrayList<ActionLog> actionLogs = new ArrayList<>();
        actionLogs.add(actionLog1);
        actionLogs.add(actionLog2);

        userRepositoryService.saveActionLogs(actionLogs);
        verify(actionLogRepository, times(1)).saveAll(actionLogs);
    }

    @Test
    void findAllActionLogListTest() {
        ObjectNode properties = JsonNodeFactory.instance.objectNode();
        properties.put("key", "value");

        ActionLog actionLog1
                = new ActionLog(1, 2, 200, 3, MeshAction.CREATE, properties);
        ActionLog actionLog2
                = new ActionLog(1, 2, 100, 3, MeshAction.CREATE, properties);
        ArrayList<ActionLog> actionLogs = new ArrayList<>();
        actionLogs.add(actionLog1);
        actionLogs.add(actionLog2);

        userRepositoryService.saveActionLogs(actionLogs);
        verify(actionLogRepository, times(1)).saveAll(actionLogs);

        when(actionLogRepository.findAll()).thenReturn(actionLogs);
        List<ActionLog> retrievedActionLogs = userRepositoryService.findAllActionLogList();
        assertEquals(actionLogs, retrievedActionLogs);
    }

    @Test
    void findAllUsersByProjectIdTest() {
        User user1 = new User(1, 1, channelHandlerContext);
        User user2 = new User(2, 1, channelHandlerContext);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.getAllByProjectId(1)).thenReturn(users);

        List<User> retrievedUsers = userRepositoryService.findAllUsersByProjectId(1);
        assertEquals(users, retrievedUsers);
    }

    @Test
    void deleteActionsInProjectTest() {
        ObjectNode properties = JsonNodeFactory.instance.objectNode();
        properties.put("key", "value");

        ActionLog actionLog1
                = new ActionLog(1, 2, 200, 3, MeshAction.CREATE, properties);
        ActionLog actionLog2
                = new ActionLog(1, 2, 100, 3, MeshAction.CREATE, properties);

        User user1 = new User(1, 1, channelHandlerContext);
        User user2 = new User(2, 1, channelHandlerContext);
        user1.appendNewAction(actionLog1);
        user2.appendNewAction(actionLog2);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.getAllByProjectId(1)).thenReturn(users);

        assertFalse(user1.getActionLogList().isEmpty());
        assertFalse(user2.getActionLogList().isEmpty());

        userRepositoryService.deleteActionsInProject(1);
        verify(userRepository, times(1)).saveAll(users);
        verify(actionLogRepository, times(1)).deleteActionLogsByProjectId(1);

        assertTrue(user1.getActionLogList().isEmpty());
        assertTrue(user2.getActionLogList().isEmpty());
    }
}