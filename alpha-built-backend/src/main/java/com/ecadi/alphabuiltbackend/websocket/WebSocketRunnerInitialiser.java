package com.ecadi.alphabuiltbackend.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for initializing and running the WebSocketServer when the Spring application context
 * is refreshed. It implements the ApplicationListener interface, meaning it listens for application events.
 * In this case, it listens for the ContextRefreshedEvent, which is published when the ApplicationContext is
 * either initialized or refreshed. This class is a Spring component and is thus a candidate for Spring's
 * dependency injection.
 * It is part of the ecadi.sem.template.example.websocket package.
 */
@Component
public class WebSocketRunnerInitialiser implements ApplicationListener<ContextRefreshedEvent> {

    /**
     * The WebSocketServer that this class will initialize and run.
     */
    private WebSocketServer webSocketServer;

    /**
     * Constructor that sets the WebSocketServer.
     *
     * @param webSocketServer the WebSocketServer to be set.
     */
    public WebSocketRunnerInitialiser(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * Method called when the ContextRefreshedEvent is published. Starts the WebSocketServer.
     *
     * @param event the ContextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        webSocketServer.run();
    }
}

