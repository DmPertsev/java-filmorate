package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor
@Valid
public class User {

    private int id;

    @Email(message = "invalid email")
    private String email;

    @NotNull
    @NotBlank(message = "login must have some letters")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    private List<Integer> friends;

    public boolean addFriendship(Integer id) {
        return friends.add(id);
    }

    public boolean removeFriendship(Integer id) {
        return friends.remove(id);
    }
}