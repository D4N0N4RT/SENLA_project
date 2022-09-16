package ru.senla.dto;

import lombok.Data;
import ru.senla.model.Role;
import ru.senla.model.User;

import javax.validation.constraints.NotBlank;

@Data
public class UserDTO implements IUserDTO {
    @NotBlank(message = "Почта (имя пользователя) не может быть пустой")
    private String username;
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotBlank(message = "Фамилия не может быть пустой")
    private String surname;
    @NotBlank(message = "Номер телефона не может отсутствовать")
    private String phone;

    public User toUser() {
        return User.builder().username(username).password(password)
                .name(name).surname(surname).phone(phone).isActive(true)
                .rating(0).role(Role.USER).build();
    }
}
