package com.ecadi.alphabuiltbackend.background;


import com.ecadi.alphabuiltbackend.domain.project.ProjectSnapshot;
import com.ecadi.alphabuiltbackend.domain.user.ActionLog;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.ecadi.alphabuiltbackend.model.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/**
 * Class InspectDatabaseTask provides the functionality to
 *      inspect and log the current state of the database at regular intervals.
 * This class is marked as a Spring component to be discovered during component scanning.
 */
@Component
public class InspectDatabaseTask {

    private final Memory memory;
    private static final Logger logger = LoggerFactory.getLogger("Inspect Database Task.");

    /**
     * InspectDatabaseTask Constructor.
     *
     * @param memory Represents the memory that holds database snapshots.
     */
    public InspectDatabaseTask(Memory memory) {
        this.memory = memory;
    }

    /**
     * This method is scheduled to run periodically and logs the current state of the database.
     */
    @Scheduled(initialDelay = 150000, fixedRate = 150000)
    public void performTask() {
        // Task logic goes here
        logger.info(String.format("Inspecting Database | Current time: %s", LocalTime.now()));
        List<ProjectSnapshot> projectSnapshotList = memory.loadAllProjectSnapsFromDatabase();
        projectSnapshotList.stream().map(ProjectSnapshot::toString).forEach(logger::info);
        List<User> userList = memory.loadAllUsersInDatabaseForLog();
        userList.stream().map(User::toString).forEach(logger::info);
        userList.forEach(user -> {
            List<ActionLog> actionLogList = user.getActionLogList();
            actionLogList.stream().map(ActionLog::toString).forEach(logger::info);
        });
    }
}
