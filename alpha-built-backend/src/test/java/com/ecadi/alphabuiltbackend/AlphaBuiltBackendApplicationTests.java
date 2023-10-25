package com.ecadi.alphabuiltbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AlphaBuiltBackendApplicationTests {

    @Test
    void contextLoads() {
        // Do Not Delete this before final delivery
        // Sometimes we need to calculate hashed password
        // Then this will be used to get hashed password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // System.out.println(encoder.encode("123456"));
    }

    @Test
    void pipelineTest() {
        assertTrue(true);
    }

    //TODO: this one takes a bit more time to run. Not sure if we should keep this.
    @Test
    void mainTest() {
        assertAll(() -> AlphaBuiltBackendApplication.main(new String[] {}));
    }

}
