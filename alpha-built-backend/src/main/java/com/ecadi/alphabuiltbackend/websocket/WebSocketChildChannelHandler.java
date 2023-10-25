package com.ecadi.alphabuiltbackend.websocket;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;


import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * This class is responsible for initializing and setting up WebSocket connections. It is a Spring component,
 * which means it's a candidate for Spring's dependency injection. The class extends the
 * ChannelInitializer class from the Netty library,
 *    meaning it is responsible for initializing a new Channel to its starting configuration.
 */
@Component
public class WebSocketChildChannelHandler extends ChannelInitializer<SocketChannel> {

    /**
     * The ChannelHandler that is responsible for processing the WebSocket connection.
     * It is annotated with @Resource, meaning Spring will attempt to find a bean with the name
     * "webSocketServerHandler" and inject it here.
     */
    @Resource(name = "webSocketServerHandler")
    private ChannelHandler webSocketServerHandler;

    /**
     * Logger for this class, used to log information and errors.
     */
    public Logger logger = LoggerFactory.getLogger(WebSocketChildChannelHandler.class);

    /**
     * Constructor that sets the ChannelHandler.
     *
     * @param webSocketServerHandler the ChannelHandler to be set.
     */
    public WebSocketChildChannelHandler(ChannelHandler webSocketServerHandler) {
        this.webSocketServerHandler = webSocketServerHandler;
    }

    /**
     * Default constructor.
     */
    public WebSocketChildChannelHandler() {

    }

    /**
     * This method is called once the Channel was registered. After the method returns, this instance
     * will be removed from the ChannelPipeline of the Channel.
     * In this implementation, it adds various handlers to the pipeline, including an HttpServerCodec, an
     * HttpObjectAggregator, a WebSocketServerProtocolHandler, a DelimiterBasedFrameDecoder, a StringDecoder,
     * a StringEncoder, and the custom webSocketServerHandler.
     *
     * @param ch the channel
     * @throws Exception if an error occurs
     */
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        logger.info(String.valueOf(ch.remoteAddress()));
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(65536));
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/"));
        // Custom handler to process WebSocket frames
        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(592048, Delimiters.lineDelimiter()));
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new StringEncoder());
        ch.pipeline().addLast("websocket-handler", webSocketServerHandler);
        logger.info("New connection");
    }

}


