package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 10, max = 200, message = "description length must be between 10 and 200 letters")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(value = 1, message = "duration must be more than 1 minute")
    private Integer duration;
    private int rate;
    @NotNull
    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();
    private List<Integer> likes = new ArrayList<>();

    public boolean addLike(Integer id) {
        return likes.add(id);
    }

    public boolean removeLike(Integer id) {
        return likes.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Film)) {
            return false;
        }
        Film film = (Film) o;
        return getId() == film.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}