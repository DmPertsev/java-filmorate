package ru.yandex.practicum.filmorate.exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String s) {
        super(s);
    }
}