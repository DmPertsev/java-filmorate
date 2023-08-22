package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.InternalException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final Validator validator;

    private static final LocalDate START_DATA = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmController(FilmService filmService, Validator validator) {
        this.filmService = filmService;
        this.validator = validator;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST запрос по адресу /films создание нового фильма: Данные запроса: '{}'", film);
        throwIfReleaseDateNotValid(film);
        validate(film);

        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма id '{}' '{}'", film.getId(), film);
        validateReleaseDate(film, "Обновление");
        throwIfReleaseDateNotValid(film);
        validate(film);

        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        List<Film> films = filmService.getAll();
        log.info("GET запрос по адресу '/films'");

        return films;
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Integer id) {
        log.info("GET запрос по адресу '/films/{}'", id);

        return filmService.findById(id);
    }


    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.info("Новый лайк фильму '{}' от '{}'", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        log.info("Удален лайк у фильма '{}' от '{}'", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping({"/popular"})
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("GET запрос по адресу '/films/popular?count={}'", count);

        return filmService.getPopularFilms(count);
    }

    public void validateReleaseDate(Film film, String text) {
        if (film.getReleaseDate().isBefore(START_DATA)) {
            throw new BadRequestException("Дата релиза не может быть раньше: " + START_DATA);
        }
        log.debug("{} фильм: '{}'", text, film.getName());
    }

    void throwIfReleaseDateNotValid(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Дата выпуска фильма: {}", film.getReleaseDate());
            throw new BadRequestException("HTTP ERROR 400: Название фильма не может быть пустым");
        }

        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new InternalException("HTTP ERROR 500: Продолжительность фильма не может быть меньше нуля");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Текущее описание фильма: {}", film.getDescription());
            throw new  BadRequestException("HTTP ERROR 400: Описание должно быть не более 200 символов");
        }
    }

    private void validate(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (ConstraintViolation<Film> filmConstraintViolation : violations) {
                messageBuilder.append(filmConstraintViolation.getMessage());
            }
            throw new BadRequestException("Ошибка валидации Фильма: " + messageBuilder);
        }
    }
}