DELETE
FROM LIKES;
DELETE
FROM FILM_GENRE;
DELETE
FROM FRIENDSHIP;
DELETE
FROM USERS;
DELETE
FROM FILMS;

ALTER TABLE USERS
    ALTER COLUMN USER_ID RESTART WITH 1;
ALTER TABLE FILMS
    ALTER COLUMN FILM_ID RESTART WITH 1;
ALTER TABLE FRIENDSHIP
    ALTER COLUMN FRIENDSHIP_ID RESTART WITH 1;
ALTER TABLE FILM_GENRE
    ALTER COLUMN FILM_GENRE_ID RESTART WITH 1;
ALTER TABLE LIKES
    ALTER COLUMN LIKE_ID RESTART WITH 1;

MERGE INTO RATING_MPA KEY (RATING_ID)
    VALUES (1, 'G', 'Нет возрастных ограничений'),
           (2, 'PG', 'Рекомендуется присутствие родителей'),
           (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
           (4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
           (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');

MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');