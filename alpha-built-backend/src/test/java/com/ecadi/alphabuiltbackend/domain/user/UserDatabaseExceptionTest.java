package com.ecadi.alphabuiltbackend.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDatabaseExceptionTest {

    @Test
    public void testUserNotExistInDatabaseException() {
        String errorMessage = "User does not exist in the database.";
        UserDatabaseException.UserNotExistInDatabaseException exception =
                new UserDatabaseException.UserNotExistInDatabaseException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }
}
