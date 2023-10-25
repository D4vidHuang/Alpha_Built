package com.ecadi.alphabuiltbackend.background;


import com.ecadi.alphabuiltbackend.model.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Class SnapshotMemoryTask provides the functionality to
 *      create and log a snapshot of the current state of memory at regular intervals.
 * This class is marked as a Spring component to be discovered during component scanning.
 */
@Component
public class SnapshotMemoryTask {

    private final Memory memory;
    private static final Logger logger = LoggerFactory.getLogger("Snapshot Memory Task.");

    /**
     * SnapshotMemoryTask Constructor.
     *
     * @param memory Represents the memory that holds database snapshots.
     */
    public SnapshotMemoryTask(Memory memory) {
        this.memory = memory;
    }

    /**
     * This method is scheduled to run periodically and logs a snapshot of the current state of memory.
     */
    @Scheduled(initialDelay = 60000, fixedRate = 60000)
    public void performTask() {
        // Task logic goes here
        logger.info(String.format("Snapshotting memory | Current time: %s", LocalTime.now()));
        memory.snapshotMemory();
    }

}
