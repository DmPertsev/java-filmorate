package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    private Validator validator = factory.getValidator();

    FilmController filmController;
    Film film;

    @BeforeEach
    void filmControllerInit() {
        filmController = new FilmController();
    }

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmController = new FilmController();
    }

    @Test
    void releaseDateBefore1895() {
        film = new Film("Наименование Фильма", "Описание фильма", LocalDate.of(1894, 01, 02), 60);

        assertThrows(BadRequestException.class, () -> filmController.throwIfReleaseDateNotValid(film));
    }

    @Test
    void emptyNameValidationTest() {
        film = new Film("", "Описание фильма", LocalDate.of(1894, 01, 02), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void blancValidationTest() {
        film = new Film(" ", "Описание фильма", LocalDate.of(1894, 01, 02), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void nullNameValidationTest() {
        film = new Film(null, "Описание фильма", LocalDate.of(1894, 01, 02), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void blancDescriptionTest() {
        film = new Film("Наименование Фильма", " ", LocalDate.of(1995, 12, 29), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void emptyDescriptionTest() {
        film = new Film("Film", "", LocalDate.of(2021, 01, 02), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void nullDescriptionTest() {
        film = new Film("Наименование Фильма", null, LocalDate.of(1995, 12, 29), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void lengthDescriptionAbove200Test() {
        film = new Film("Название", "Lorem ipsum dolor sit amet," +
                "consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
                "Ut enim ad minim veniam," + "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur." +
                "Excepteur sint occaecat cupidatat non proident," + "sunt in culpa qui officia deserunt mollit anim id est laborum.",
                LocalDate.of(1894, 01, 02), 60);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    void durationNotNullTest() {
        film = new Film("Наименование Фильма", "Описание фильма", LocalDate.of(1995, 12, 29), 0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertThat(violations.size()).isEqualTo(1);
    }
}