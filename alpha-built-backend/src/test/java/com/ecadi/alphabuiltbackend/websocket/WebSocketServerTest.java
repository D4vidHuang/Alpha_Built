package com.ecadi.alphabuiltbackend.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * test class for WebSocketServer.
 */
public class WebSocketServerTest {

    /**
     * test case for .run() method
     */
    @Test
    public void runTest() {
        // setup mocks
        WebSocketServer webSocketServer = new WebSocketServer(new WebSocketChildChannelHandler());
        WebSocketServer spyServer = Mockito.spy(webSocketServer);
        // invoke target method
        spyServer.run();
        // verify method invocations
        verify(spyServer, times(1))
                .build();
    }

    /**
     * test case for .build(). Since it handles all exceptions, we check no exception is thrown.
     */
    @Test
    public void buildTest_assertNoExceptionWasThrown() {
        // create server
        WebSocketServer webSocketServer = new WebSocketServer(new WebSocketChildChannelHandler());
        // run and assert no exception was thrown since .build() catches all exceptions.
        assertAll(webSocketServer::build);
        WebSocketServer webSocketServer2 = new WebSocketServer(null);
        assertAll(webSocketServer::build);
    }

    /**
     * test case for .getChildChannelHandler()
     */
    @Test
    public void getChildChannelHandlerTest() {
        WebSocketChildChannelHandler webSocketChildChannelHandler = new WebSocketChildChannelHandler();
        WebSocketServer webSocketServer = new WebSocketServer(webSocketChildChannelHandler);
        ChannelHandler handler = webSocketServer.getChildChannelHandler();
        // assert true
        assertThat(handler).isEqualTo(webSocketChildChannelHandler);
        // change handler
        WebSocketChildChannelHandler handler2 = new WebSocketChildChannelHandler();
        webSocketServer.setChildChannelHandler(handler2);
        // assert new handler
        assertThat(webSocketServer.getChildChannelHandler()).isEqualTo(handler2);
        // change to handler3
        WebSocketChildChannelHandler handler3 = new WebSocketChildChannelHandler();
        webSocketServer.setChildChannelHandler(handler3);
        // assert handler3
        assertThat(webSocketServer.getChildChannelHandler()).isEqualTo(handler3);
    }

    /**
     * test case for .setChildChannelHandler()
     */
    @Test
    public void setChildChannelHandlerTest() {
        WebSocketChildChannelHandler handler1 = new WebSocketChildChannelHandler();
        WebSocketServer webSocketServer = new WebSocketServer(handler1);
        WebSocketChildChannelHandler handler2 = new WebSocketChildChannelHandler();
        webSocketServer.setChildChannelHandler(handler2);
        // assert handler2
        assertThat(webSocketServer.getChildChannelHandler()).isEqualTo(handler2);
        WebSocketChildChannelHandler handler3 = new WebSocketChildChannelHandler();
        webSocketServer.setChildChannelHandler(handler3);
        // assert handler3
        assertThat(webSocketServer.getChildChannelHandler()).isEqualTo(handler3);
        WebSocketChildChannelHandler handler4 = new WebSocketChildChannelHandler();
        webSocketServer.setChildChannelHandler(handler4);
        // assert handler4
        assertThat(webSocketServer.getChildChannelHandler()).isEqualTo(handler4);
    }

    /**
     * test case for .getPort() method
     */
    @Test
    public void getPortTest() {
        WebSocketChildChannelHandler webSocketChildChannelHandler = new WebSocketChildChannelHandler();
        WebSocketServer webSocketServer = new WebSocketServer(webSocketChildChannelHandler);
        int port = webSocketServer.getPort();
        assertThat(port).isEqualTo(8888);
    }

    /**
     * test case for .setPort() method
     */
    @Test
    public void setPortTest() {
        WebSocketChildChannelHandler webSocketChildChannelHandler = new WebSocketChildChannelHandler();
        WebSocketServer webSocketServer = new WebSocketServer(webSocketChildChannelHandler);
        int port = webSocketServer.getPort();
        assertThat(port).isEqualTo(8888);
        // change to 1234
        webSocketServer.setPort(1234);
        port = webSocketServer.getPort();
        assertThat(port).isEqualTo(1234);
        // change to 1111
        webSocketServer.setPort(1111);
        port = webSocketServer.getPort();
        assertThat(port).isEqualTo(1111);
    }

    @Test
    public void testClose() {
        WebSocketChildChannelHandler webSocketChildChannelHandler = new WebSocketChildChannelHandler();
        WebSocketServer webSocketServer = new WebSocketServer(webSocketChildChannelHandler);

        // Mock Channel and ChannelFuture
        Channel channel = mock(Channel.class);
        ChannelFuture serverChannelFuture = mock(ChannelFuture.class);

        // Set the return behavior for the channel and serverChannelFuture
        when(serverChannelFuture.channel()).thenReturn(channel);
        when(channel.close()).thenReturn(serverChannelFuture);

        // Use reflection or a setter method in WebSocketServer to set the serverChannelFuture field
        ReflectionTestUtils.setField(webSocketServer, "serverChannelFuture", serverChannelFuture);

        // Call the method being tested
        webSocketServer.close();

        // Verify interactions
        verify(serverChannelFuture, times(1)).channel();
        verify(channel, times(1)).close();
    }
}
