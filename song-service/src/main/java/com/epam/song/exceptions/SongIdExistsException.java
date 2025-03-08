package com.epam.song.exceptions;

public class SongIdExistsException extends RuntimeException {
    public SongIdExistsException(String message) {
        super(message);
    }
}
