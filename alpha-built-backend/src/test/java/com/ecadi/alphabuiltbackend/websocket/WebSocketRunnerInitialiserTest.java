package com.ecadi.alphabuiltbackend.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextRefreshedEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test class for WebSocketRunnerInitialiser.
 */
public class WebSocketRunnerInitialiserTest {

    /**
     * Emulates the situation where an applicationEvent is fired. Should trigger the .run() method exactly one time.
     *
     * @throws Exception thrown exception
     */
    @Test
    public void onApplicationEventTest() throws Exception {
        // setup mocks
        WebSocketServer mockWebSocketServer = mock(WebSocketServer.class);
        WebSocketRunnerInitialiser webSocketRunnerInitialiser = new WebSocketRunnerInitialiser(mockWebSocketServer);
        ContextRefreshedEvent mockEvent = mock(ContextRefreshedEvent.class);
        // invoke target method
        webSocketRunnerInitialiser.onApplicationEvent(mockEvent);
        // verify method invocations
        verify(mockWebSocketServer, times(1))
                .run();
    }
}
