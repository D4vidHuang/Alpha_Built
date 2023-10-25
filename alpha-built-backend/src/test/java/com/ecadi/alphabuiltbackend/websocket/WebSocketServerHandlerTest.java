package com.ecadi.alphabuiltbackend.websocket;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadata;
import com.ecadi.alphabuiltbackend.model.Memory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.channel.Channel;
import org.mockito.Mock;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;

public class WebSocketServerHandlerTest {


    private WebSocketServerHandler handler;

    @Mock
    private Memory memory;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private Channel channel;

    @Mock
    private WebSocketServerHandshaker handshaker;

    @Mock
    private Logger logger;

    private ChannelFuture future;

    /**
     * test case for .channelActive() method
     *
     * @throws Exception thrown by this method.
     */
    @Test
    public void channelActiveTest() throws Exception {
        // prepare mocks
        WebSocketServerHandler webSocketServerHandler = new WebSocketServerHandler(mock(Memory.class));
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        webSocketServerHandler.channelActive(context);
        // verify context is called
        verify(context, times(1))
                .channel();
    }

    @Test
    public void testChannelRead0_HttpRequest() throws Exception {
        logger = mock(Logger.class);
        ctx = mock(ChannelHandlerContext.class);
        channel = mock(Channel.class);
        handshaker = mock(WebSocketServerHandshaker.class);
        memory = mock(Memory.class);
        handler = new WebSocketServerHandler(memory);
        WebSocketServerHandler webSocketServerHandler = new WebSocketServerHandler(memory);
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        Object msg = mock(Object.class);
        when(msg.toString()).thenReturn("HttpRequest");
        webSocketServerHandler.channelRead0(context, msg);
    }

    /**
     * set up testing environment before each test case.
     */
    @BeforeEach
    public void setup() {
        handler = new WebSocketServerHandler(memory);
        ctx = mock(ChannelHandlerContext.class);
        channel = mock(Channel.class);
        when(ctx.channel()).thenReturn(channel);
        future = mock(ChannelFuture.class);
        when(ctx.channel()).thenReturn(channel);
        when(channel.write(any(TextWebSocketFrame.class))).thenReturn(future);
        memory = mock(Memory.class);
    }

    @Test
    public void testHandleWebSocketFrame_PingWebSocketFrame() {
        PingWebSocketFrame pingFrame = new PingWebSocketFrame();
        handler.handleWebSocketFrame(ctx, pingFrame);
        verify(channel, times(1)).write(any(PongWebSocketFrame.class));
    }

    @Test
    public void testHandleWebSocketFrame_TextWebSocketFrame() {
        String msg = "{\"key\":\"value\"}";  // a simple JSON message
        TextWebSocketFrame textFrame = new TextWebSocketFrame(msg);
        handler.handleWebSocketFrame(ctx, textFrame);
        // verify your expected outcome for TextWebSocketFrame
    }

    @Test
    public void testHandleWebSocketFrame_UnsupportedWebSocketFrame() {
        BinaryWebSocketFrame binaryFrame = new BinaryWebSocketFrame();
        try {
            handler.handleWebSocketFrame(ctx, binaryFrame);
        } catch (UnsupportedOperationException e) {
            assertEquals("Unsupported frame type: " + binaryFrame.getClass().getName(), e.getMessage());
        }
    }

    @Test
    public void testSendMessageToClient() {
        String message = "Test message";
        int userId = 123;

        // When flushing channel, we also assume it's successful and call the listener
        doAnswer(invocation -> {
            ((ChannelFutureListener) invocation.getArgument(0)).operationComplete(future);
            return null;
        }).when(future).addListener(any(ChannelFutureListener.class));

        // Assume the write operation is successful
        when(future.isSuccess()).thenReturn(true);

        handler.sendMessageToClient(ctx, message, userId);

        // Verify that the write method was called with the expected TextWebSocketFrame
        verify(channel, times(1)).write(new TextWebSocketFrame(message));
        // Verify that the flush method was called
        verify(channel, times(1)).flush();
    }

    @Test
    public void testVerdictMeshMetadata_whenMemoryProcessSucceeds() {
        WebSocketServerHandler webSocketServerHandler = new WebSocketServerHandler(memory);
        int userId = 123;
        int projectId = 456;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode on = mapper.createObjectNode();
        on.put("key", "value");
        MeshMetadata metadata = new MeshMetadata(1, MeshAction.CREATE, on);

        when(memory.processMeshMetaDataForProject(userId, projectId, metadata)).thenReturn(true);

        webSocketServerHandler.verdictMeshMetadata(userId, projectId, metadata);

        verify(memory, times(1)).registerMeshMetadataForUser(metadata, projectId, userId);
    }

}
