package com.ecadi.alphabuiltbackend.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The method to create a new websocket server.
 */
@Component
public class WebSocketServer implements Runnable {
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * The boss group.
     */
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();

    /**
     * The worker group.
     */
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * The server bootstrap.
     */
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private int port = 8888;
    private ChannelHandler childChannelHandler;
    private ChannelFuture serverChannelFuture;


    /**
     *  The constructor.
     *
     * @param childChannelHandler the child channel handler
     */
    public WebSocketServer(WebSocketChildChannelHandler childChannelHandler) {
        this.childChannelHandler = childChannelHandler;
    }

    /**
     * The constructor.
     */

    @Override
    public void run() {
        build();
    }

    /**
     * The method to build the websocket server.
     */
    public void build() {
        try {
            long begin = System.currentTimeMillis();
            // Settting up the serverBootstrap
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(childChannelHandler);
            long end = System.currentTimeMillis();
            logger
                    .info("Netty Websocket has started and it takes "
                            + (end - begin) + " ms，the port is " + port);

            // Start the server
            serverChannelFuture = serverBootstrap.bind(port).sync();
        } catch (Exception e) {
            System.out.println(e);
            logger.info(e.getMessage());
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            e.printStackTrace();
        }

    }


    /**
     * The method to close the websocket server.
     */
    public void close() {
        serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();

        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
        } catch (InterruptedException ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * The method to get the child channel handler.
     *
     * @return the child channel handler
     */
    public ChannelHandler getChildChannelHandler() {
        return childChannelHandler;
    }

    /**
     * The method to set the child channel handler.
     *
     * @param childChannelHandler the child channel handler
     */
    public void setChildChannelHandler(ChannelHandler childChannelHandler) {
        this.childChannelHandler = childChannelHandler;
    }

    /**
     * The method to get the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * The method to set the port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }
}

