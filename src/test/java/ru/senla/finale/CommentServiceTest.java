package ru.senla.finale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.senla.exception.EmptyResponseException;
import ru.senla.model.Comment;
import ru.senla.model.Post;
import ru.senla.repository.CommentRepository;
import ru.senla.service.CommentService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    private CommentService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new CommentService(commentRepository);
    }

    @Test
    public void create() {
        Comment comment = new Comment();
        comment.setContent("Test");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");
        comment.setPost(post);

        underTest.create(comment);

        ArgumentCaptor<Comment> captor =
                ArgumentCaptor.forClass(Comment.class);

        Mockito.verify(commentRepository).save(captor.capture());

        Comment captured = captor.getValue();
        Assertions.assertEquals(captured, comment);
    }

    @Test
    public void findByPost() throws EmptyResponseException {
        Comment comment = new Comment();
        comment.setContent("Test");

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");
        comment.setPost(post);

        Mockito.when(commentRepository.findAllByPost(post)).thenReturn(new ArrayList<>(List.of(comment)));

        List<Comment> check = underTest.findAllByPost(post);

        Mockito.verify(commentRepository).findAllByPost(post);
        Assertions.assertNotNull(check);
        Assertions.assertEquals(1, check.size());
    }
}
