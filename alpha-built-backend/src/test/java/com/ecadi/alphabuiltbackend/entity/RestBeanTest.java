package com.ecadi.alphabuiltbackend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestBeanTest {

    @Test
    public void testSuccessNoPayload() {
        RestBean<String> restBean = RestBean.success();
        assertEquals(200, restBean.getCode());
        assertEquals(true, restBean.isSuccess());
        assertEquals(null, restBean.getMessage());
    }

    @Test
    public void testSuccessWithPayload() {
        String payload = "Successful operation";
        RestBean<String> restBean = RestBean.success(payload);

        assertEquals(200, restBean.getCode());
        assertEquals(true, restBean.isSuccess());
        assertEquals(payload, restBean.getMessage());
    }

    @Test
    public void testFailureNoPayload() {
        int errorCode = 404;
        RestBean<String> restBean = RestBean.failure(errorCode);

        assertEquals(errorCode, restBean.getCode());
        assertEquals(false, restBean.isSuccess());
        assertEquals(null, restBean.getMessage());
    }

    @Test
    public void testFailureWithPayload() {
        int errorCode = 500;
        String payload = "Server error";
        RestBean<String> restBean = RestBean.failure(errorCode, payload);

        assertEquals(errorCode, restBean.getCode());
        assertEquals(false, restBean.isSuccess());
        assertEquals(payload, restBean.getMessage());
    }

    @Test
    public void testSuccessResponseGetters() {
        String payload = "Successful operation";
        RestBean<String> restBean = RestBean.success(payload);

        assertEquals(200, restBean.getCode());
        assertEquals(true, restBean.isSuccess());
        assertEquals(payload, restBean.getMessage());
    }

    @Test
    public void testFailureResponseGetters() {
        int errorCode = 500;
        String payload = "Server error";
        RestBean<String> restBean = RestBean.failure(errorCode, payload);

        assertEquals(errorCode, restBean.getCode());
        assertEquals(false, restBean.isSuccess());
        assertEquals(payload, restBean.getMessage());
    }

    @Test
    public void testEqualsAndHashCode() {
        String payload = "Payload";
        RestBean<String> restBean1 = RestBean.success(payload);
        RestBean<String> restBean2 = RestBean.success(payload);
        RestBean<String> restBean3 = RestBean.failure(500, "Error");

        assertEquals(restBean1, restBean2);
        assertEquals(restBean1.hashCode(), restBean2.hashCode());

        assertNotEquals(restBean1, restBean3);
        assertNotEquals(restBean1.hashCode(), restBean3.hashCode());
    }

    @Test
    public void testToString() {
        String payload = "Payload";
        RestBean<String> restBean = RestBean.success(payload);

        String expected = "RestBean(status=200, success=true, message=Payload)";
        assertEquals(expected, restBean.toString());

        assertNotNull(restBean.toString()); // toString should never return null
    }
}