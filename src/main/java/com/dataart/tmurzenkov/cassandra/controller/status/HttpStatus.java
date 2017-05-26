package com.dataart.tmurzenkov.cassandra.controller.status;

/**
 * Holds Http statuses for the Swagger Annotations.
 *
 * @author tmurzenkov
 */
public interface HttpStatus {
    int CREATED = 201;
    int FOUND = 302;
    int BAD_REQUEST = 400;
    int NOT_FOUND = 404;
    int CONFLICT = 409;
}
