package ru.senla.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthDTO {
    @NotBlank(message = "Почта (имя пользователя) не может быть пустой")
    private String username;
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
