package ru.senla.finale.unit.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.senla.model.Comment;
import ru.senla.model.User;
import ru.senla.model.Post;
import ru.senla.repository.CommentRepository;
import ru.senla.repository.PostRepository;
import ru.senla.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ru.senla.model.Category.ESTATE;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testBootstrapping() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Comment com = Comment.builder().user(us).content("Test").post(post)
                .time(LocalDateTime.now()).build();
        Assertions.assertNull(com.getId());
        em.persist(com);
        Assertions.assertNotNull(com.getId());
    }

    @Test
    void testSave() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Comment com = Comment.builder().user(us).content("Test").post(post)
                .time(LocalDateTime.now()).build();
        Assertions.assertNull(com.getId());
        underTest.save(com);
        Assertions.assertNotNull(com.getId());
    }

    @Test
    void testDelete() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Comment com = Comment.builder().user(us).content("Test").post(post)
                .time(LocalDateTime.now()).build();
        Assertions.assertNull(com.getId());
        underTest.save(com);
        Assertions.assertNotNull(com.getId());

        underTest.delete(com);

        List<Comment> check = underTest.findAll();

        Assertions.assertEquals(check.size(), 0);
    }

    @Test
    void testGetByUser() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        userRepository.save(us);
        postRepository.save(post);
        Comment com = Comment.builder().user(us).content("Test").post(post)
                .time(LocalDateTime.now()).build();
        underTest.save(com);

        List<Comment> check = underTest.findAllByUser(us);

        Assertions.assertNotNull(check);
        Assertions.assertEquals(check.size(), 1);
    }

    @Test
    void testGetByPost() {
        User us = User.builder().username("mail@mail.ru").password("pass").build();
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        userRepository.save(us);
        postRepository.save(post);
        Comment com = Comment.builder().user(us).content("Test").post(post)
                .time(LocalDateTime.now()).build();
        underTest.save(com);

        List<Comment> check = underTest.findAllByPost(post);

        Assertions.assertNotNull(check);
        Assertions.assertEquals(check.size(), 1);
    }
}
