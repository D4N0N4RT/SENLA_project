package ru.senla.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Setter;
import ru.senla.model.Category;

import java.time.LocalDate;

@Setter
@Builder
public class GetPostDTO {
    private long id;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Category category;
    private double price;
    private String userEmail;
    private int rating;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate postingDate;
    private String description;
}
