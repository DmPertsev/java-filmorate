package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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
    Film film;

    private Validator validator = factory.getValidator();

    FilmController filmController;

    @BeforeEach
    void filmControllerInit() {
        filmController = new FilmController();
    }

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmController = new FilmController();

        film = new Film();
        film.setId(1);
        film.setName("Spider-Man: No Way Home");
        film.setDescription("Yet another movie");
        film.setReleaseDate(LocalDate.of(2021, 12, 16));
        film.setDuration(148);
    }

    @Test
    void filmWithoutNameNull() {
        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void filmWithoutNameEmptyString() {
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void filmWthDescriptionWithMore200Symbols() {
        film.setDescription("аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа" +
                "ааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа" +
                "аааааааааааааааааааааааааааааааааааааааааааааааааааааааа");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void releaseDateIsBefore28December1895() {
        film.setReleaseDate(LocalDate.of(1894, 12,28));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void durationFilmNegative() {
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}