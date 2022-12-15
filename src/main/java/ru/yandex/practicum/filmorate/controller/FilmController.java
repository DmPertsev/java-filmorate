package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("POST запрос: /films добавить фильм: Данные запроса: '{}'", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Обновлен фильм id: '{}' '{}'", film.getId(), film);
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET запрос: '/films'");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable String id) {
        log.info("GET запрос по адресу '/films/{}'", id);
        return filmService.findById(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable String filmId, @PathVariable String userId) {
        log.info("Новый лайк фильму '{}' от '{}'", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable String filmId, @PathVariable String userId) {
        log.info("Удален лайк у фильма '{}' от '{}'", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping({"/popular?count={count}", "/popular"})
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") String count) {
        log.info("GET запрос по адресу '/films/popular?count={}'", count);
        return filmService.getPopularFilms(count);
    }
}