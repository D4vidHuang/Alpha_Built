package com.ecadi.alphabuiltbackend.intercommunication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InterMessageTypeTest {

    @Test
    public void testEnumValues() {
        assertNotNull(InterMessageType.HELLO);
        assertNotNull(InterMessageType.BYE);
        assertNotNull(InterMessageType.GEO);
        assertNotNull(InterMessageType.HELLO_RESPONSE);
        assertNotNull(InterMessageType.BYE_RESPONSE);
        assertNotNull(InterMessageType.CTRLZ);
    }
}
