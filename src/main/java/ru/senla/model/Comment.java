package ru.senla.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.senla.dto.CommentDTO;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @SequenceGenerator(name = "commentsIdSeq",
            sequenceName = "comments_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentsIdSeq")
    //@GeneratedValue(strategy = GenerationType.IDENTITY) // For tests
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_email")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    private String content;

    private LocalDateTime time;

    public CommentDTO toDTO() {
        return CommentDTO.builder().author(this.user.getName() + ' ' + this.user.getSurname()
                        + " (" + this.user.getUsername() + ')')
                .content(this.content).time(this.time).build();
    }
}
