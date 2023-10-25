package com.ecadi.alphabuiltbackend.websocket;

import com.ecadi.alphabuiltbackend.intercommunication.InterMessage;
import com.ecadi.alphabuiltbackend.intercommunication.InterMessageType;
import com.ecadi.alphabuiltbackend.intercommunication.MessageParser;
import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadata;
import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadataUtil;

import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.ecadi.alphabuiltbackend.model.Memory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


import io.netty.util.CharsetUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class handles WebSocket connections. It extends SimpleChannelInboundHandler,
 *      a Netty class for handling channels (connections).
 * This class is designed to manage WebSocket connections.
 * WebSocket is a communication protocol that provides full-duplex communication channels over a single TCP connection.
 * It is decorated with @Component and @Sharable, which are Spring Framework annotations.
 */
@Component
@Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private long userId = -1;
    private WebSocketServerHandshaker handshaker;
    private Memory memory;
    private List<ChannelHandlerContext> contextList = new ArrayList<>();

    /**
     * Constructs a WebSocketServerHandler with given Memory instance.
     *
     * @param memory - Memory instance that will be used for storing user information and other data.
     */
    public WebSocketServerHandler(Memory memory) {
        this.memory = memory;
    }


    /**
     * Method that is called when a connection is established.
     * It logs the event and sends a message to the client.
     *
     * @param ctx the ChannelHandlerContext associated with this event
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // The channel is active, indicating a WebSocket connection has been established
        Channel clientChannel = ctx.channel();
        //System.out.println(clientChannel);
        LOGGER.info("WebSocket connection established");
        // Send a message to the client
        String message = "WebSocket connection established";
        contextList.add(ctx);
    }


    /**
     * Method that is called when a message is received.
     * It distinguishes between HTTP requests and WebSocket frames.
     *
     * @param ctx the ChannelHandlerContext associated with this event
     * @param msg the message received
     */

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        //LOGGER.info(receivedString);
        {
            LOGGER.info(String.format("New message received: %s", msg.toString()));
        }
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof TextWebSocketFrame) {
            handleWebSocketFrame(ctx, (TextWebSocketFrame) msg);
        }
    }

    /**
     * Handles different types of WebSocket frames.
     *
     * @param ctx the ChannelHandlerContext associated with this event
     * @param frame the WebSocket frame received
     */
    void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("Unsupported frame type: " + frame.getClass().getName());
        }
        String message = frame.content().toString(CharsetUtil.UTF_8);
        {
            LOGGER.info(String.format("New message received: %s", message));
        }
        // Handle text frame

        try {
            JsonNode parsedMessage = new ObjectMapper().readTree(message);
            InterMessage parseInMemoryMessage = MessageParser.parseInterMessage(parsedMessage);
            interpInterMessage(ctx, parseInMemoryMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /**
     * Sends a message to a client.
     *
     * @param context the ChannelHandlerContext associated with the client
     * @param message the message to send
     */
    void sendMessageToClient(ChannelHandlerContext context, String message, int userId) {
        ChannelFuture future = context.channel().write(new TextWebSocketFrame(message));
        context.channel().flush();
        future.addListener((ChannelFutureListener) fut -> {
            if (fut.isSuccess()) {
                LOGGER.info("The server handler has successfully sent the message." + userId);
            } else {
                try {
                    throw fut.cause();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Processes the mesh metadata for a specific project and adds a verdict based on the processing result.
     *
     * @param projectId The ID of the project.
     * @param metadata  The MeshMetadata object representing the metadata to be processed.
     */
    void verdictMeshMetadata(int userId, int projectId, MeshMetadata metadata) {
        try {
            boolean result = memory.processMeshMetaDataForProject(userId, projectId, metadata);
            metadata.addVerdict(result);
            if (result) {
                memory.registerMeshMetadataForUser(metadata, projectId, userId);
            }
        } catch (Exception e) {
            metadata.addVerdict(false);
            e.printStackTrace();
        }
    }

    /**
     * Handles different types of InterCommunication messages.
     * The behavior depends on the type of message (HELLO, GEO, BYE).
     *
     * @param ctx the ChannelHandlerContext associated with this event
     * @param parseInMemoryMessage the InterCommunication message to interpret
     */
    private void interpInterMessage(ChannelHandlerContext ctx, InterMessage parseInMemoryMessage) {
        switch (parseInMemoryMessage.getType()) {
            case HELLO:
                int currUserId = parseInMemoryMessage.getUserId();
                int currProjectId = parseInMemoryMessage.getProjectId();
                User currUser = null;
                Project currProject = null;
                if (!memory.checkProjectExist(currProjectId)) {
                    currProject = memory.createProject(currProjectId);
                }

                if (!memory.checkProjectActive(currProjectId)) {
                    System.out.println("hereeee");
                    currProject = memory.loadProject(currProjectId);
                } else {
                    currProject = memory.getProjectHandler(currProjectId);
                }
                if (memory.checkUserActive(currUserId)) {
                    LOGGER.error("User has already existed in memory");
                    memory.removeUserFromMemory(currUserId);
                    // Show raise a bug.
                }
                if (!memory.checkUserExist(currUserId)) {
                    currUser = memory.createUser(currUserId, currProjectId, ctx);
                } else {
                    currUser = memory.loadUser(currUserId, ctx);
                    if (currUser.getChannelHandlerContext() == null) {
                        currUser.setChannelHandlerContext(ctx);
                    }
                }

                if (currProject == null) {
                    String errMessage = String.format("Project is null when loading user with user id %d.", currUserId);
                    LOGGER.error(errMessage);
                    throw new CommunicationException.NullProjectException(errMessage);
                }
                assert currUser != null;
                currProject.addActiveUser(currUser);
                List<MeshMetadata> initialMeshMetadataList = MeshMetadataUtil.createInitialMeshMetadataList(currProject);

                String logInfo = String
                        .format(
                                "Sending initial mesh metadata from project "
                                        + "with project id %d to user with "
                                        + "use id %d with %d length mesh metadata list.",
                                currUserId,
                                currProjectId,
                                initialMeshMetadataList.size()
                        );
                LOGGER.info(logInfo);

                InterMessage outMessage = new InterMessage(
                        InterMessageType.HELLO_RESPONSE,
                        parseInMemoryMessage.getUserId(),
                        parseInMemoryMessage.getProjectId(),
                        initialMeshMetadataList
                );
                try {
                    String parsedOutMessage = new ObjectMapper().writeValueAsString(outMessage);
                    sendMessageToClient(ctx, parsedOutMessage, currUser.getUserId());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                break;
            case GEO:
                int geoUserId = parseInMemoryMessage.getUserId();
                int geoProjectId = parseInMemoryMessage.getProjectId();
                assert memory.checkUserBelongToProjectById(geoUserId, geoProjectId);
                Project project = memory.getProjectHandler(geoProjectId);
                parseInMemoryMessage.getMeshMetadata()
                        .forEach(meshMetadata -> verdictMeshMetadata(geoUserId, geoProjectId, meshMetadata));
                ExecutorService executorService = Executors.newFixedThreadPool(project.getActiveUserList().size());
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                project.getActiveUserList().forEach(user -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            sendMessageToClient(
                                    user.getChannelHandlerContext(),
                                    new ObjectMapper().writeValueAsString(parseInMemoryMessage),
                                    user.getUserId()
                            );
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }, executorService);
                    futures.add(future);
                });
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                allFutures.whenComplete((result, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                    } else {
                        LOGGER.info("All messages sent successfully");
                    }
                });
                executorService.shutdown();

                break;
            case BYE:
                int byeUserId = parseInMemoryMessage.getUserId();
                int byeProjectId = parseInMemoryMessage.getProjectId();
                // TODO: What if the page is refreshed ?
                memory.removeUserFromProject(byeUserId, byeProjectId);
                memory.removeUserFromMemory(byeUserId);
                if (memory.checkActiveUsersInProject(byeProjectId) == 0) {
                    String logMessage = String
                            .format("Project with project id %d has been removed from memory.", byeProjectId);
                    LOGGER.info(logMessage);
                    memory.removeProjectFromMemory(byeProjectId);
                }
                ctx.close();
                break;
            default:
                throw new CommunicationException.InvalidMessageTypeException(
                        "Unexpected value: " + parseInMemoryMessage.getType()
                );
        }
    }

    /**
     * Handles an incoming HTTP request and performs the WebSocket handshake if applicable.
     *
     * @param ctx The ChannelHandlerContext object associated with the channel.
     * @param req The FullHttpRequest object representing the incoming HTTP request.
     * @throws Exception If an error occurs during the execution of the method.
     */
    void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    LOGGER.info("The server handler has successfully sent the message.");
                } else {
                    try {
                        throw future.cause();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Constructs the WebSocket location URL based on the HTTP request.
     *
     * @param req The FullHttpRequest object representing the incoming HTTP request.
     * @return The WebSocket location URL.
     */
    private String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaders.Names.HOST) + "/websocket";
        return "ws://" + location;
    }

    /**
     * Overrides the exceptionCaught method from the ChannelInboundHandlerAdapter class.
     * This method is called when an exception is caught during the processing of a channel operation.
     *
     * @param ctx   The ChannelHandlerContext object associated with the channel.
     * @param cause The Throwable object representing the cause of the exception.
     * @throws Exception If an error occurs during the execution of the method.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * Overrides the channelInactive method from the ChannelInboundHandlerAdapter class.
     * This method is called when the channel becomes inactive, indicating that the channel is closed or disconnected.
     *
     * @param ctx The ChannelHandlerContext object associated with the channel.
     * @throws Exception If an error occurs during the execution of the method.
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //SocketManagement.closeUser_ChannelHandlerContext(userId);

    }

}




