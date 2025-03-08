package com.epam.song.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(SongIdExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage songIdExistsException(SongIdExistsException ex, WebRequest request) {
        log.error("409 Status Code", ex);

        List<String> message = new ArrayList<>();
        message.add(ex.getMessage());

        return new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new Date(),
                message,
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("400 Status Code", ex);

        List<String> errors = ex.getBindingResult().getFieldErrors().stream().filter(Objects::nonNull)
                .map(m -> (m.getField() + " " + m.getDefaultMessage())).toList();

        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                errors,
                request.getDescription(false));
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage numberFormatException(NumberFormatException ex, WebRequest request) {
        log.error("400 Status Code", ex);
        List<String> message = new ArrayList<>();
        message.add("Invalid ID format. Could not parse all IDs from the CSV string.");

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
        message.add(ex.getMessage());

        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
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
