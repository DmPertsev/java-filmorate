package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        log.info("Список фильмов отправлен");
        return filmStorage.findAll();
    }

    public Film getById(int id) {
        if (!filmStorage.getAll().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        log.info("Фильм с id: {} отправлен", id);
        return filmStorage.getById(id);
    }

    public Film deleteById(int id) {
        if (!filmStorage.getAll().containsKey(id)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден, удаление невозможно");
        }
        log.info("Фильм с id: {} удален", id);
        return filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {
        if (!filmStorage.getAll().containsKey(filmId)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        filmStorage.getById(filmId).getUsersLikes().add(userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);
        return filmStorage.getById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        if (!filmStorage.getAll().containsKey(filmId)) {
            throw new NotFoundException("HTTP ERROR 404: Фильм не найден");
        }
        if (!filmStorage.getById(filmId).getUsersLikes().contains(userId)) {
            throw new NotFoundException("HTTP ERROR 404: Нет лайка от пользователя");
        }
        filmStorage.getById(filmId).getUsersLikes().contains(userId);
        log.info("Пользователь с id: {} удалил лайк фильму с id {}", userId, filmId);
        return filmStorage.getById(filmId);
    }
    public List<Film> getPopularFilms(int count) {
        log.info("Список популярных фильмов отправлен");

        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUsersLikes().size(), o1.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}