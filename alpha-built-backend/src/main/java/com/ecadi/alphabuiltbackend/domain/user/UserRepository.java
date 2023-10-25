package com.ecadi.alphabuiltbackend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This interface defines the contract for database operations that can be performed on a User entity.
 * It extends JpaRepository, thus inheriting a large number of operations for working with User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a User entity from the database using the provided userId.
     *
     * @param userId The id of the user to be retrieved from the database.
     * @return A User entity if found, otherwise null.
     */
    public User getUserByUserId(int userId);

    public List<User> getAllByProjectId(int projectId);
}
