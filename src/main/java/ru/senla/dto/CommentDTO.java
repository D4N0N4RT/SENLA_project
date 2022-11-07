package ru.senla.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Builder
public class CommentDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd-MM-yyyy")
    private LocalDateTime time;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String content;
}
