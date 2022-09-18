package ru.senla.dto;

import lombok.Data;
import ru.senla.model.Category;

@Data
public class UpdatePostDTO {
    private String title;
    private String description;
    private double price;
    private Category category;
}
