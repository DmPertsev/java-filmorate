package ru.yandex.practicum.filmorate.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String s) {
        super(s);
    }
}