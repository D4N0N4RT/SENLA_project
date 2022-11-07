package ru.senla.dto;

import lombok.Builder;
import lombok.Getter;
import ru.senla.model.Category;

import javax.validation.constraints.Size;

@Getter
@Builder
public class UpdatePostDTO {
    @Size(min = 1, max = 100, message = "Название не может отсутствовать или быть длинее 100 символов")
    private String title;
    @Size(min = 1, message = "Описание не может быть пустым")
    private String description;
    private double price;
    private Category category;
}
