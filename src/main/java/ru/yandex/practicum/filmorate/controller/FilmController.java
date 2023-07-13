package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.ConflictException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    private static final LocalDate DATE_BEFORE = LocalDate.of(1895, 12, 28);
    final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        throwIfReleaseDateNotValid(film);
        throwIfAlreadyExist(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм: {} успешно добавлен в коллекцию", film.getName());

        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        throwIfReleaseDateNotValid(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные о фильме: " + film + ", такого фильма в базе нет");
        }
        throwIfAlreadyExist(film);
        films.put(film.getId(), film);
        log.info("Информация о фильме: {} успешно обновлена", film.getName());

        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    void throwIfReleaseDateNotValid(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(DATE_BEFORE) || film.getDuration() < 0) {
            log.warn("Дата выпуска фильма: {}\nПродолжительность фильма: {}", film.getReleaseDate(), film.getDuration());
            throw new BadRequestException("До 28 декабря 1895 года кино не производили или продолжительность неверная");
        }
    }

    private void throwIfAlreadyExist(@RequestBody Film filmToAdd) {
        boolean exists = films.values().stream()
                .anyMatch(film -> isAlreadyExist(filmToAdd, film));
        if (exists) {
            log.warn("Фильм к добавлению: {}", filmToAdd);
            throw new ConflictException("HTTP ERROR 409: Фильм: " + filmToAdd + " уже существует в коллекции");
        }
    }

    private boolean isAlreadyExist(Film filmToAdd, Film film) {
        return filmToAdd.getName().equals(film.getName()) &&
                filmToAdd.getReleaseDate().equals(film.getReleaseDate());
    }
}