package ru.senla.dto;

import lombok.Data;
import ru.senla.model.Category;
import ru.senla.model.Post;
import ru.senla.model.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CreatePostDTO {
    @NotBlank(message = "Название не может быть пустым")
    private String title;
    @NotBlank(message = "Описание может быть пустым")
    private String description;
    @NotNull(message = "Цена не может быть нулевой")
    @Min(value = 1, message = "Цена должна быть больше нуля")
    private double price;
    @NotNull(message = "Объявление не может быть без категории")
    private Category category;

    public Post toPost(User user) {
        return Post.builder().title(title).description(description).price(price)
                .promotion(0).sold(false).category(category).user(user)
                .rating(user.getRating()).postingDate(LocalDate.now()).build();
    }
}
