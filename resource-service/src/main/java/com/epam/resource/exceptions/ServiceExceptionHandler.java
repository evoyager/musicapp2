package com.epam.resource.exceptions;

import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(String.format("Invalid format of ID: %s", ex.getValue()));

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(InvalidIdException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage invalidIdException(InvalidIdException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(String.format("Invalid ID value: %s, it can't be zero or negative.", ex.getMessage()));

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

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

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage badRequestException(BadRequestException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add("Invalid audio data.");

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage unsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add("Invalid media type.");

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("400 Status Code", ex);

        List<String> collect = ex.getBindingResult().getFieldErrors().stream().filter(Objects::nonNull)
                .map(m -> (m.getField() + " " + m.getDefaultMessage())).toList();
        List<String> message = new ArrayList<>(collect);

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage httpClientErrorException(HttpClientErrorException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());

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
}
