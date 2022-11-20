package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    void createFilmFailName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 12, 28));
        film.setDuration(100);

        RuntimeException trow = assertThrows(RuntimeException.class, () -> {
            filmController.createNewFilm(film);});
        assertEquals("Название фильма не может быть пустым", trow.getMessage());
    }

    @Test
    void createFilmFailReleaseDate() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        RuntimeException trow = assertThrows(RuntimeException.class, () -> {
            filmController.createNewFilm(film);});
        assertEquals("До 28 декабря 1895 года кино не производили", trow.getMessage());
    }

    @Test
    void createFilmFailForNegativeOrZeroDuration() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 12, 28));
        film.setDuration(-100);

        RuntimeException trow = assertThrows(RuntimeException.class, () -> {
            filmController.createNewFilm(film);});
        assertEquals("Продолжительность фильма не может быть меньше нуля", trow.getMessage());
    }
}