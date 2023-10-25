package com.ecadi.alphabuiltbackend.rpc.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HeartBeatTask implements Runnable {

    private final HeartBeatService heartBeatService;
    private static final List<String> peerUrls = List.of("http://localhost:8082/hello");
    private static final Logger logger = LoggerFactory.getLogger(String.class);

    public HeartBeatTask(HeartBeatService heartBeatService) {
        this.heartBeatService = heartBeatService;
    }

    @Override
    public void run() {
        return;
    }
}
