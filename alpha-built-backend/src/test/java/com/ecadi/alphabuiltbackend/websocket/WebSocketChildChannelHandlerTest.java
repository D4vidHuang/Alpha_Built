package com.ecadi.alphabuiltbackend.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * testing class for WebSocketChildChannelHandler class.
 */
class WebSocketChildChannelHandlerTest {

    /**
     * test for initChannel. The logger and SocketChannel should be called with correct parameter and called correct times.
     *
     * @throws Exception thrown by this method
     */
    @Test
    public void initChannelTest() throws Exception {
        // setup mocks
        SocketChannel ch = mock(SocketChannel.class);
        // a mock for a channelPipeline that is returned by ch.pipeline()
        ChannelPipeline cp = mock(ChannelPipeline.class);
        when(ch.pipeline()).thenReturn(cp);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(411);
        when(ch.remoteAddress())
                .thenReturn(inetSocketAddress);
        when(cp.addLast(any(HttpServerCodec.class)))
                .thenReturn(null);

        ChannelHandler channelHandler = mock(ChannelHandler.class);
        WebSocketChildChannelHandler webSocketChildChannelHandler = new WebSocketChildChannelHandler(channelHandler);
        Logger mockLogger = mock(Logger.class);
        webSocketChildChannelHandler.logger = mockLogger;
        // invoke the being tested method
        webSocketChildChannelHandler.initChannel(ch);
        // verify method invocations
        verify(cp, times(6))
                .addLast(any());
        verify(cp, times(1))
                .addLast(eq("websocket-handler"), any());
        verify(mockLogger, times(1))
                .info(String.valueOf(ch.remoteAddress()));
        verify(mockLogger, times(1))
                .info("New connection");
    }
}