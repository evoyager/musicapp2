package com.epam.resource.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("404 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(String.format("Resource not found %s", ex.getMessage()));

        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(NoSuchBucketException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage noSuchBucketException(NoSuchBucketException ex, WebRequest request) {
        log.error("404 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());

        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage resourceNotFoundException(IllegalStateException ex, WebRequest request) {
        log.error("500 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());

        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                message,
                request.getDescription(false));
    }
}
