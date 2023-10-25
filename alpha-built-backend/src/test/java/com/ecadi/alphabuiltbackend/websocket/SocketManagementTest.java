package com.ecadi.alphabuiltbackend.websocket;

import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SocketManagementTest {
    @Test
    public void testUserRegistrationAndRetrieval() {
        long userId = 1L;
        ChannelHandlerContext mockContext = Mockito.mock(ChannelHandlerContext.class);

        assertFalse(SocketManagement.isUserIdRegistered(userId));

        SocketManagement.addNewUser_ChannelHandlerContext(userId, mockContext);

        assertTrue(SocketManagement.isUserIdRegistered(userId));
        assertEquals(mockContext, SocketManagement.getUser_ChannelHandlerContext(userId));
    }

    @Test
    public void testUserRemoval() {
        long userId = 2L;
        ChannelHandlerContext mockContext = Mockito.mock(ChannelHandlerContext.class);

        SocketManagement.addNewUser_ChannelHandlerContext(userId, mockContext);

        SocketManagement.closeUser_ChannelHandlerContext(userId);

        assertFalse(SocketManagement.isUserIdRegistered(userId));
    }

    @Test
    public void testGetUser_ChannelHandlerContextForNonExistentUser() {
        long userId = 3L;

        assertThrows(IllegalArgumentException.class, () -> SocketManagement.getUser_ChannelHandlerContext(userId));
    }

    @Test
    public void testCloseUser_ChannelHandlerContextForNonExistentUser() {
        long userId = 4L;

        assertThrows(IllegalArgumentException.class, () -> SocketManagement.closeUser_ChannelHandlerContext(userId));
    }
}