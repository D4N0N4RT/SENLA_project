package ru.senla.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
