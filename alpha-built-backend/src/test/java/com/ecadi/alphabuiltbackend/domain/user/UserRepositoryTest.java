package com.ecadi.alphabuiltbackend.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUserByUserId() {
        // Create and save a test User.
        User testUser = new User(1, 1, null);
        userRepository.save(testUser);

        // Retrieve the User.
        User retrievedUser = userRepository.getUserByUserId(1);

        // Check that the User was retrieved successfully.
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUserId()).isEqualTo(1);
    }
}
