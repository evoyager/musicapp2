package com.epam.storage.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(NoSuchBucketException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage bucketNotFoundException(NoSuchBucketException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(String.format("S3 bucket not found %s", ex.getMessage()));

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(NoSuchKeyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage noSuchKeyException(NoSuchKeyException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(String.format("S3 key not found %s", ex.getMessage()));

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(InvalidCsvException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage invalidCsvException(InvalidCsvException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage illegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }
}
