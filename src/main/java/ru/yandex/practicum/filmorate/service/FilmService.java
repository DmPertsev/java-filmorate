package ru.yandex.practicum.filmorate.service;

import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private static final LocalDate DATE_BEFORE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        throwIfReleaseDateNotValid(film);
        if (filmStorage.isNotExist(film.getId())) {
            throw new NotFoundException("HTTP ERROR 404: Невозможно обновить данные о фильме, так как такого фильма у нас нет");
        }

        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        log.info("Список фильмов отправлен");

        return filmStorage.findAll();
    }

    public Film getById(int id) {
        filmStorage.isNotExist(id);
        log.info("Фильм с id: {} отправлен", id);

        return filmStorage.findById(id);
    }

    public Film deleteById(int id) {
        filmStorage.isNotExist(id);
        log.info("Фильм с id: {} удален", id);

        return filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {
        filmStorage.isNotExist(filmId);
        filmStorage.findById(filmId).getUsersLikes().add(userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);

        return filmStorage.findById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        filmStorage.isNotExist(filmId);

        if (!filmStorage.findById(filmId).getUsersLikes().contains(userId)) {
            throw new NotFoundException("HTTP ERROR 404: Нет лайка от пользователя");
        }
        filmStorage.findById(filmId).getUsersLikes().contains(userId);
        log.info("Пользователь с id: {} удалил лайк фильму с id {}", userId, filmId);

        return filmStorage.findById(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Список популярных фильмов отправлен");

        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUsersLikes().size(), o1.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void throwIfReleaseDateNotValid(Film film) {

        if (film.getName().isBlank()) {
            log.warn("Дата выпуска фильма: {}", film.getReleaseDate());
            throw new BadRequestException("HTTP ERROR 400: Название фильма не может быть пустым");
        }

        if (film.getReleaseDate().isBefore(DATE_BEFORE)) {
            log.warn("Дата выпуска фильма: {}", film.getReleaseDate());
            throw new BadRequestException("HTTP ERROR 400: До 28 декабря 1895 года кино не производили");
        }

        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new InternalException("HTTP ERROR 500: Продолжительность фильма не может быть меньше нуля");
        }

        if (film.getDescription().length() > 200) {
            log.warn("Текущее описание фильма: {}", film.getDescription());
            throw new BadRequestException("HTTP ERROR 400: Описание должно быть не более 200 символов");
        }
    }

    public void throwIfAlreadyExist(Film filmToAdd) {
        boolean exists = filmStorage.findAll().stream()
                .anyMatch(film -> isAlreadyExist(filmToAdd, film));
        if (exists) {
            log.warn("Фильм к добавлению: {}", filmToAdd);
            throw new ConflictException("HTTP ERROR 409: Такой фильм уже существует в коллекции");
        }
    }

    private boolean isAlreadyExist(Film filmToAdd, Film film) {
        return filmToAdd.getName().equals(film.getName()) &&
                filmToAdd.getReleaseDate().equals(film.getReleaseDate());
    }
}