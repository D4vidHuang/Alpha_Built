package com.ecadi.alphabuiltbackend.rpc.heartbeat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecadi.alphabuiltbackend.rpc.messageinfo.Message;
import com.ecadi.alphabuiltbackend.rpc.messageinfo.MessageType;
import com.ecadi.alphabuiltbackend.rpc.SendRequestService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HeartBeatServiceTest {

    @Mock
    private SendRequestService sendRequestService;
    @Mock
    private ThreadPoolTaskScheduler taskScheduler;
    @InjectMocks
    private HeartBeatService heartBeatService;

    @Test
    public void testInvoke() {
        sendRequestService = Mockito.mock(SendRequestService.class);
        taskScheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        heartBeatService = new HeartBeatService(sendRequestService, taskScheduler);
        String url = "http://localhost:8080";
        Message testMessage = new Message(MessageType.HEARTBEAT, new ObjectMapper().createObjectNode());
        ResponseEntity<Message> testResponse = new ResponseEntity<>(testMessage, HttpStatus.OK);
        when(sendRequestService.sendMessagesToRemote(any(Message.class), eq(url))).thenReturn(testResponse);

        ResponseEntity<Message> response = heartBeatService.invoke(url);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        verify(sendRequestService, times(1)).sendMessagesToRemote(any(Message.class), eq(url));
    }

    @Test
    public void testInvoke_resourceAccessException() {
        sendRequestService = Mockito.mock(SendRequestService.class);
        taskScheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        heartBeatService = new HeartBeatService(sendRequestService, taskScheduler);
        String url = "http://localhost:8080";
        Message testMessage = new Message(MessageType.HEARTBEAT, new ObjectMapper().createObjectNode());
        ResponseEntity<Message> testResponse = new ResponseEntity<>(testMessage, HttpStatus.OK);
        when(sendRequestService.sendMessagesToRemote(any(Message.class), eq(url)))
                .thenThrow(ResourceAccessException.class);

        ResponseEntity<Message> response = heartBeatService.invoke(url);
        //TODO: assert the println() message
    }

    @Test
    public void testInvoke_httpStatusCodeException() {
        sendRequestService = Mockito.mock(SendRequestService.class);
        taskScheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        heartBeatService = new HeartBeatService(sendRequestService, taskScheduler);
        String url = "http://localhost:8080";
        Message testMessage = new Message(MessageType.HEARTBEAT, new ObjectMapper().createObjectNode());
        ResponseEntity<Message> testResponse = new ResponseEntity<>(testMessage, HttpStatus.OK);

        when(sendRequestService.sendMessagesToRemote(any(Message.class), eq(url)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ResponseEntity<Message> response = heartBeatService.invoke(url);
        //TODO: assert the println() message

    }

    @Test
    public void testInvoke_otherException() {
        sendRequestService = Mockito.mock(SendRequestService.class);
        taskScheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
        heartBeatService = new HeartBeatService(sendRequestService, taskScheduler);
        String url = "http://localhost:8080";
        Message testMessage = new Message(MessageType.HEARTBEAT, new ObjectMapper().createObjectNode());
        ResponseEntity<Message> testResponse = new ResponseEntity<>(testMessage, HttpStatus.OK);

        when(sendRequestService.sendMessagesToRemote(any(Message.class), eq(url)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_GATEWAY));

        ResponseEntity<Message> response = heartBeatService.invoke(url);
        //TODO: assert the println() message

    }

}