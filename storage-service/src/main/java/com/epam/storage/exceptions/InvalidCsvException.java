package com.epam.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCsvException extends RuntimeException {

    /**
     * Constructor for ResourceNotFoundException with a specific message.
     *
     * @param message explanatory message describing the error.
     */
    public InvalidCsvException(String message) {
        super(message);
    }

    /**
     * Constructor for ResourceNotFoundException with a specific message and root cause.
     *
     * @param message explanatory message describing the error.
     * @param cause   the throwable that caused this exception to be thrown.
     */
    public InvalidCsvException(String message, Throwable cause) {
        super(message, cause);
    }
}
