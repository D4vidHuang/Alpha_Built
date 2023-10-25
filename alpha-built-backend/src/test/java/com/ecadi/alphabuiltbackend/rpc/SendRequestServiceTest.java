package com.ecadi.alphabuiltbackend.rpc;

import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

class SendRequestServiceTest {

    @Mock
    private RestTemplate restTemplate;

//    //TODO: Fix this test
//    @Test
//    public void sendMessagesToRemoteTest() {
//            // Given
//            String url = "http://localhost:8080/test";
//            ObjectMapper mapper = new ObjectMapper();
//            restTemplate = new RestTemplate();
//            Message message = new Message(MessageType.HEARTBEAT, mapper.createObjectNode());
//
//            ResponseEntity<Message> expectedResponse = new ResponseEntity<>(message, HttpStatus.OK);
//            SendRequestService sendRequestService = new SendRequestService(restTemplate);
//            ResponseEntity<Message> actualResponse = sendRequestService.sendMessagesToRemote(message, url);
//
//            assertThrows(
//                    ConnectException.class,
//                    () -> {
//                        sendRequestService.sendMessagesToRemote(message, url);
//                    });
//
//    }
}