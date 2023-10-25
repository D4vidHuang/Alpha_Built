package com.ecadi.alphabuiltbackend.websocket;

import io.netty.channel.ChannelHandlerContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SocketManagement {
    public static Map<Long, ChannelHandlerContext> onlineUserMap =
            new ConcurrentHashMap<Long, ChannelHandlerContext>();

    /**
     * Register a new user that just has established a web socket connection with the server.
     *
     * @param userId the new userId that just established a web socket connection with the server.
     * @param newContext the channel handler context that is used to communicate with the client. */
    public static void addNewUser_ChannelHandlerContext(Long userId, ChannelHandlerContext
            newContext) {
        onlineUserMap.put(userId, newContext);
    }

    /** Get the channel handler context belonging to a specific user.
     *
     * @param userId the user id that we would like to query its channel handler context.
     * @return the channel handler context that is corresponding to the user id.
     * @throws IllegalArgumentException if the user is not registered.
     * */
    @SuppressWarnings("checkstyle:NeedBraces")
    public static ChannelHandlerContext getUser_ChannelHandlerContext(long userId) {
        if (!onlineUserMap.containsKey(userId)) {
            throw new IllegalArgumentException();
        } else {
            return onlineUserMap.get(userId);
        }
    }

    /** Remove the channel handler context belonging to a specific user when he/she quits the game
     * or finish the multiplayer game.
     *
     * @param userId the user id that we would like to query its channel handler context.
     * @throws IllegalArgumentException if the user is not registered.
     * */
    @SuppressWarnings("checkstyle:NeedBraces")
    public static void closeUser_ChannelHandlerContext(long userId) {
        if (!onlineUserMap.containsKey(userId)) {
            throw new IllegalArgumentException();
        } else {
            onlineUserMap.remove(userId);
        }
    }

    /** Interface for testing: test whether a given user id has established a web socket connection.
     *
     * @param userId the user Id that will be tested.
     * @return true if the user id has been stored in the onlineUserMap (established a web socket
     *     connection)
     * */
    public static boolean isUserIdRegistered(long userId) {
        return onlineUserMap.containsKey(userId);
    }
}


