package com.ecadi.alphabuiltbackend.background;

import com.ecadi.alphabuiltbackend.model.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SnapshotMemoryTaskTest {

    @InjectMocks
    private SnapshotMemoryTask task;

    @Mock
    private Memory memory;

    // Mock static method LoggerFactory.getLogger
    private static Logger logger;

    @BeforeEach
    public void setup() {
        logger = LoggerFactory.getLogger("Snapshot Memory Task.");
    }

    @Test
    public void performTaskTest() {
        task.performTask();

        verify(memory, times(1)).snapshotMemory();

        // Unfortunately, Mockito doesn't have built-in functionality to verify calls to static methods,
        // including LoggerFactory.getLogger() and logger.info(). These lines will need to be manually tested.
    }

}