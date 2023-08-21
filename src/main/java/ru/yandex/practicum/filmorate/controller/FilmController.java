package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private static final LocalDate START_DATA = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Optional<Film> create(@Valid @RequestBody Film film) {
        log.info("POST запрос по адресу /films создание нового фильма: Данные запроса: '{}'", film);
        validateReleaseDate(film, "Создание");

        return filmService.create(film);
    }

    @PutMapping
    public Optional<Film> update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма id '{}' '{}'", film.getId(), film);
        validateReleaseDate(film, "Обновление");

        return filmService.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        List<Film> films = filmService.getAll();
        log.info("GET запрос по адресу '/films'");

        return films;
    }

    @GetMapping("/{id}")
    public Optional<Film> findById(@PathVariable String id) {
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

    public void validateReleaseDate(Film film, String text) {
        if (film.getReleaseDate().isBefore(START_DATA)) {
            throw new BadRequestException("Дата релиза не может быть раньше: " + START_DATA);
        }
        log.debug("{} фильм: '{}'", text, film.getName());
    }
}