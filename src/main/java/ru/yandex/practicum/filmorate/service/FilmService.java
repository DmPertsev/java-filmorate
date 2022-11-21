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

    public Film findById(int id) {
        filmStorage.isExist(id);
        log.info("Фильм с id: {} отправлен", id);
        return filmStorage.findById(id);
    }

    public Film deleteById(int id) {
        filmStorage.isExist(id);
        log.info("Фильм с id: {} удален", id);
        return filmStorage.deleteById(id);
    }

    public Film addLike(int filmId, int userId) {
        filmStorage.isExist(filmId);
        filmStorage.findById(filmId).getUsersLikes().add(userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id {}", userId, filmId);
        return filmStorage.findById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        filmStorage.isExist(filmId);
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
}