package com.ecadi.alphabuiltbackend.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This service class handles the interactions between the application and the User repository.
 * It provides methods for retrieving, checking and adding User entities.
 */
@Service
public class UserRepositoryService {

    /**
     * A Logger instance to record important information and errors.
     */
    Logger logger = LoggerFactory.getLogger("User Repository Service Logger.");

    /**
     * An instance of UserRepository for interacting with the database.
     */
    @Autowired
    UserRepository userRepository;

    @Autowired
    ActionLogRepository actionLogRepository;

    /**
     * Retrieves a User entity from the database by its userId.
     *
     * @param userId The id of the User to be retrieved.
     * @return A User entity if found, otherwise throws an exception.
     * @throws UserDatabaseException.UserNotExistInDatabaseException If the User is not found in the database.
     */
    public User getUserByUserId(int userId) {
        User loadedUser = userRepository.getUserByUserId(userId);
        if (loadedUser == null) {
            String errMessage = String.format("User with user id %d does not exist in database.", userId);
            logger.error(errMessage);
            throw new UserDatabaseException.UserNotExistInDatabaseException(errMessage);
        }
        return loadedUser;
    }

    /**
     * Checks if a User entity exists in the database by its userId.
     *
     * @param userId The id of the User to be checked.
     * @return true if the User exists, false otherwise.
     */
    public boolean checkUserExistingInDatabase(int userId) {
        User loadedUser = userRepository.getUserByUserId(userId);
        return loadedUser != null;
    }

    /**
     * Adds a new User entity to the database.
     *
     * @param user The User entity to be added.
     */
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    /**
     * Deletes all User entities from the database.
     */
    public void clearDatabase() {
        userRepository.deleteAll();
        actionLogRepository.deleteAll();
    }

    /**
     * Saves the given ActionLog entity into the database.
     *
     * @param actionLog The ActionLog entity to be saved.
     */
    public void saveActionLog(ActionLog actionLog) {
        actionLogRepository.save(actionLog);
    }

    /**
     * Saves the given list of ActionLog entities into the database.
     *
     * @param actionLogList The list of ActionLog entities to be saved.
     */
    public void saveActionLogs(List<ActionLog> actionLogList) {
        actionLogRepository.saveAll(actionLogList);
    }

    /**
     * Fetches all ActionLog entities from the database.
     *
     * @return A list of all ActionLog entities in the database.
     */
    public List<ActionLog> findAllActionLogList() {
        return actionLogRepository.findAll();
    }

    /**
     * Deletes all ActionLog entities related to a specific project from the database,
     * and clears all ActionLog instances from all Users associated with the same project.
     *
     * @param projectId The ID of the project whose ActionLog entities are to be deleted.
     */
    @Transactional
    public void deleteActionsInProject(int projectId) {
        List<User> userList = userRepository.getAllByProjectId(projectId);
        userList.forEach(User::clearActionLogs);
        userRepository.saveAll(userList);
        actionLogRepository.deleteActionLogsByProjectId(projectId);
    }

    /**
     * Fetches all User entities associated with a specific project from the database.
     *
     * @param projectId The ID of the project whose User entities are to be fetched.
     * @return A list of all User entities associated with the specified project.
     */
    public List<User> findAllUsersByProjectId(int projectId) {
        return userRepository.getAllByProjectId(projectId);
    }

}
