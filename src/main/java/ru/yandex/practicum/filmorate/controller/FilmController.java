package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int id = 1;
    private static final LocalDate DATE_COUNTING_START = LocalDate.of(1895, 12, 28);
    final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);
        filmExistCheck(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Ошибка, данный фильм отсуствует");
        }
        filmExistCheck(film);
        films.put(film.getId(), film);
        log.info("Обновлена информация о фильме: {}", film.getName());
        return film;
    }

    @GetMapping
    public Collection<Film> getListOfFilms() {
        return films.values();
    }

    void validate(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE_COUNTING_START) || film.getDuration() < 0) {
            log.warn("Дата выпуска: {}\nПродолжительность: {}", film.getReleaseDate(), film.getDuration());
            throw new ValidationException("Тогда еще фильмы не снимали или неверная продолжительность");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма: {}", film.getDescription());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

    }

    private void filmExistCheck(@RequestBody Film filmToAdd) {
        boolean exists = films.values().stream()
                .anyMatch(film -> isAlreadyExist(filmToAdd, film));
        if (exists) {
            log.warn("Добавить фильм: {}", filmToAdd);
            throw new ValidationException("Такой фильм уже есть");
        }
    }

    private boolean isAlreadyExist(Film filmToAdd, Film film) {
        return filmToAdd.getName().equals(film.getName()) &&
                filmToAdd.getReleaseDate().equals(film.getReleaseDate());
    }
}