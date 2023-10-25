package com.ecadi.alphabuiltbackend.rpc.heartbeat;

import com.ecadi.alphabuiltbackend.rpc.messageinfo.Message;
import com.ecadi.alphabuiltbackend.rpc.messageinfo.MessageType;
import com.ecadi.alphabuiltbackend.rpc.SendRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.concurrent.TimeUnit;

/**
 * Represents the HeartBeat service responsible for sending heartbeat messages to a remote server
 * at a periodic interval. It uses the SendRequestService to send the heartbeat message and handles
 * various exceptions that may occur during the process.
 */
@Service
public class HeartBeatService {

    private final transient SendRequestService requestService;
    private final transient ThreadPoolTaskScheduler taskScheduler;
    private static final Logger logger = LoggerFactory.getLogger(Message.class);

    /**
     * Constructs a HeartBeatService with the specified SendRequestService and ThreadPoolTaskScheduler.
     * It initializes and configures the task scheduler to execute the HeartBeatTask at a periodic interval.
     *
     * @param requestService The SendRequestService used to send the heartbeat message.
     * @param taskScheduler  The ThreadPoolTaskScheduler used to schedule the heartbeat task.
     */
    public HeartBeatService(SendRequestService requestService, ThreadPoolTaskScheduler taskScheduler) {
        this.requestService = requestService;
        this.taskScheduler = taskScheduler;
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(10, TimeUnit.SECONDS);
        this.taskScheduler.setPoolSize(3);
        this.taskScheduler.initialize();
        this.taskScheduler.schedule(new HeartBeatTask(this), periodicTrigger);
    }

    /**
     * Sends a heartbeat message to the specified URL and returns the response.
     *
     * @param url The URL to send the heartbeat message to.
     * @return The ResponseEntity containing the response message.
     * @throws ResourceAccessException if the server does not respond.
     * @throws HttpStatusCodeException if an HTTP error occurs (e.g., 404, 500, etc.).
     * @throws Exception               if any other exception occurs.
     */
    public ResponseEntity<Message> invoke(String url) {
        try {
            Message hearBeatMessage = new Message(MessageType.HEARTBEAT, new ObjectMapper().createObjectNode());
            ResponseEntity<Message> response = requestService.sendMessagesToRemote(hearBeatMessage, url);
            System.out.println(response);
            logger.info(String.valueOf(response));
            return response;
        } catch (ResourceAccessException e) {
            // Handle the case when the server does not respond
            System.err.println("Server did not respond: " + e.getMessage());
        } catch (HttpStatusCodeException e) {
            // Handle HTTP errors (e.g., 404, 500, etc.)
            int statusCode = e.getRawStatusCode();
            String statusText = e.getStatusText();
            System.err.println("HTTP error: " + statusCode + " " + statusText);
        } catch (Exception e) {
            // Handle any other exceptions
            System.err.println("An error occurred: " + e.getMessage());
        }
        return null;
    }
}
