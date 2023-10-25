package com.ecadi.alphabuiltbackend.entity;

import lombok.Data;

/**
 * This is a generic class for structuring RESTful API responses.
 * It uses Lombok's @Data annotation to automatically generate
 * getters, setters, equals(), hashCode(), and toString() methods.
 *
 * @param <T> the type of the data payload in the response
 */
@Data
public class RestBean<T> {

    /**
     * The HTTP status code of the response.
     */
    private int status;

    /**
     * A boolean indicating whether the operation was successful.
     */
    private boolean success;

    /**
     * The data payload of the response.
     */
    private T message;

    /**
     * Private constructor used by the factory methods.
     *
     * @param status the HTTP status code of the response
     * @param success a boolean indicating whether the operation was successful
     * @param message the data payload of the response
     */
    private RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    /**
     * Returns a successful RestBean with no data payload.
     *
     * @param <T> the type of the data payload in the response
     * @return a successful RestBean
     */
    public static <T> RestBean<T> success() {
        return new RestBean<>(200, true, null);
    }

    /**
     * Returns a successful RestBean with a data payload.
     *
     * @param data the data payload of the response
     * @param <T> the type of the data payload in the response
     * @return a successful RestBean
     */
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, true, data);
    }

    /**
     * Returns a failure RestBean with no data payload.
     *
     * @param status the HTTP status code of the response
     * @param <T> the type of the data payload in the response
     * @return a failure RestBean
     */
    public static <T> RestBean<T> failure(int status) {
        return new RestBean<>(status, false, null);
    }

    /**
     * Returns a failure RestBean with a data payload.
     *
     * @param status the HTTP status code of the response
     * @param data the data payload of the response
     * @param <T> the type of the data payload in the response
     * @return a failure RestBean
     */
    public static <T> RestBean<T> failure(int status, T data) {
        return new RestBean<>(status, false, data);
    }

    public int getCode() {
        return status;
    }
}
