package com.ecadi.alphabuiltbackend.rpc;

import com.ecadi.alphabuiltbackend.rpc.messageinfo.Message;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Represents a service for sending requests to a remote server using a RestTemplate.
 * It provides a method to send messages to the remote server and receive a response.
 */
@Service
public class SendRequestService {

    private final transient RestTemplate restTemplate;

    /**
     * Constructs a SendRequestService with the specified RestTemplate.
     *
     * @param restTemplate The RestTemplate used to send requests.
     */
    public SendRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends a message to the specified URL as an HTTP POST request and returns the response.
     *
     * @param message The message to send.
     * @param url     The URL to send the message to.
     * @return The ResponseEntity containing the response message.
     */
    public ResponseEntity<Message> sendMessagesToRemote(Message message, String url) {
        HttpEntity<Message> messageHttpEntity = new HttpEntity<>(message);
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                messageHttpEntity,
                new ParameterizedTypeReference<Message>() {}
        );
    }
}
