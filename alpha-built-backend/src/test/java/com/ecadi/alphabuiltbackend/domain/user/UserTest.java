package com.ecadi.alphabuiltbackend.domain.user;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserTest {

    @Test
    public void testUser() {
        ChannelHandlerContext context = null; // Replace with proper context if needed
        User user1 = new User(1, 1, context);
        User user2 = new User(2, 1, context);

        user1.setId(1L);
        assertEquals(1L, user1.getId());
        user2.setId(1L);

        assertEquals(1, user1.getUserId());
        assertEquals(1, user1.getProjectId());
        assertEquals(context, user1.getChannelHandlerContext());

        assertNotEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setId(3L);
        assertNotEquals(user1, user2);
    }

    private ChannelHandlerContext channelHandlerContext;

    private User userUnderTest;

    @BeforeEach
    void setUp() {
        channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        userUnderTest = new User(1, 1, channelHandlerContext);

    }

    @Test
    void getId() {
        userUnderTest.setId(1L);
        assertEquals(1L, userUnderTest.getId());
    }

    @Test
    void setId() {
        userUnderTest.setId(1L);
        assertEquals(1L, userUnderTest.getId());
    }

    @Test
    void testEquals() {
        User user1 = new User(1, 1, channelHandlerContext);
        User user2 = new User(1, 1, channelHandlerContext);
        user1.setId(1L);
        user2.setId(1L);
        assertEquals(user1, user1);
        assertEquals(user1, user2);
        assertNotEquals(user1, null);
        assertNotEquals(user1, new User());
    }

    @Test
    void testHashCode() {
        User user1 = new User(1, 1, channelHandlerContext);
        User user2 = new User(1, 1, channelHandlerContext);
        user1.setId(1L);
        user2.setId(1L);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void getUserId() {
        assertEquals(userUnderTest.getUserId(), 1);
    }

    @Test
    void getProjectId() {
        assertEquals(userUnderTest.getProjectId(), 1);
    }

    @Test
    void getChannelHandlerContext() {
        assertEquals(userUnderTest.getChannelHandlerContext(), channelHandlerContext);
    }

    @Test
    void setUserId() {
        userUnderTest.setUserId(2);
        assertEquals(userUnderTest.getUserId(), 2);
    }

    @Test
    void setProjectId() {
        userUnderTest.setProjectId(2);
        assertEquals(userUnderTest.getProjectId(), 2);
    }

    @Test
    void setChannelHandlerContext() {
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        userUnderTest.setChannelHandlerContext(context);
        assertEquals(userUnderTest.getChannelHandlerContext(), context);
    }

    @Test
    void testToString() {
        assertEquals(userUnderTest.toString(), "User(id=null, userId=1, projectId=1, channelHandlerContext="
                + channelHandlerContext.toString() + ", actionLogList=[])");
    }

    @Test
    void appendNewActionTest() {
        User user = new User(1, 1, channelHandlerContext);
        assertTrue(user.getActionLogList().isEmpty());
        user.appendNewAction(new ActionLog());
        assertFalse(user.getActionLogList().isEmpty());
    }

    @Test
    void clearActionLogs() {
        User user = new User(1, 1, channelHandlerContext);
        user.appendNewAction(new ActionLog());
        assertFalse(user.getActionLogList().isEmpty());
        user.clearActionLogs();
        assertTrue(user.getActionLogList().isEmpty());
    }

    @Test
    void clearStaleActionLogs() {

        ObjectNode properties = JsonNodeFactory.instance.objectNode();
        properties.put("key", "value");

        ActionLog actionLog1
                = new ActionLog(1, 2, 200, 3, MeshAction.CREATE, properties);
        ActionLog actionLog2
                = new ActionLog(1, 2, 100, 3, MeshAction.CREATE, properties);

        User user = new User(1, 1, channelHandlerContext);
        user.appendNewAction(actionLog1);
        user.appendNewAction(actionLog2);

        assertEquals(2, user.getActionLogList().size());

        user.clearStaleActionLogs(150);
        assertEquals(1, user.getActionLogList().size());
    }
}