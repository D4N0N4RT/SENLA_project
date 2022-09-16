package ru.senla.dto;

import lombok.Data;

@Data
public class UpdateUserDTO implements IUserDTO {
    private String password;
    private String name;
    private String surname;
    private String phone;
}
