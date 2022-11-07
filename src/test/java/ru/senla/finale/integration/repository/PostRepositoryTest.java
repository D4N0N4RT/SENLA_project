package ru.senla.finale.integration.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.senla.model.Post;
import ru.senla.model.User;
import ru.senla.repository.PostRepository;
import ru.senla.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.senla.model.Category.ESTATE;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository underTest;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testBootstrapping() {
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post.getId());
        em.persist(post);
        Assertions.assertNotNull(post.getId());
    }

    @Test
    void shouldSave() {
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post.getId());
        underTest.save(post);
        Assertions.assertNotNull(post.getId());
    }

    @Test
    void shouldUpdate() {
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post.getId());
        underTest.save(post);
        Assertions.assertNotNull(post.getId());

        post.setPrice(200);

        underTest.save(post);

        Post post1 = underTest.findById(1L).get();

        Assertions.assertEquals(post1.getPrice(), 200);
        Assertions.assertEquals(post1.getCategory(), ESTATE);
    }

    @Test
    void shouldDelete() {
        Post post = Post.builder().title("Test").description("Test")
                .price(100).postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post.getId());
        underTest.save(post);
        Assertions.assertNotNull(post.getId());

        underTest.delete(post);

        List<Post> check = underTest.findAll();

        Assertions.assertEquals(check.size(), 0);
    }

    @Test
    void shouldFindAllByUser() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Test").description("Test")
                .user(us).rating(0).price(100).sold(false)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Post post2 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(200).sold(false)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllByUserAndSoldOrderByPromotionDesc(us, false);
        Assertions.assertEquals(check.size(), 2);
    }

    @Test
    void shouldFindAllByCategory() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Test").description("Test")
                .user(us).rating(0).price(100).sold(false)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Post post2 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(200).sold(false)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllByCategoryAndSoldOrderByPromotionDescRatingDesc(ESTATE, false);
        Assertions.assertEquals(check.get(1), post1);
        Assertions.assertEquals(check.get(0), post2);
    }

    @Test
    void shouldFindAllByPriceGreaterThan() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Test").description("Test")
                .user(us).rating(0).price(100).sold(false)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Post post2 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(200).sold(false).promotion(10)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Post post3 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(160).sold(false).promotion(1000)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        underTest.save(post3);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllBySoldAndPriceGreaterThanOrderByPromotionDescRatingDesc(false,150);
        Assertions.assertEquals(check.size(), 2);
        Assertions.assertEquals(check.get(1), post2);
    }

    @Test
    void shouldFindAllByPriceLessThan() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Test").description("Test")
                .user(us).rating(10).price(100).sold(false).promotion(10)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Post post2 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(200).sold(false).promotion(10)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Post post3 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(160).sold(false).promotion(1000)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        underTest.save(post3);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllBySoldAndPriceLessThanOrderByPromotionDescRatingDesc(false,210);
        Assertions.assertEquals(check.size(), 3);
        Assertions.assertEquals(check.get(0), post3);
        Assertions.assertEquals(check.get(1), post1);
        Assertions.assertEquals(check.get(2), post2);
    }

    @Test
    void shouldFindAllByPostingDateIsAfter() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Test").description("Test")
                .user(us).rating(0).price(100).sold(false).promotion(22)
                .postingDate(LocalDate.now().minusDays(3)).category(ESTATE).build();
        Post post2 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(200).sold(false).promotion(1000)
                .postingDate(LocalDate.now().plusDays(6)).category(ESTATE).build();
        Post post3 = Post.builder().title("Test").description("Test")
                .user(us).rating(2).price(160).sold(false).promotion(1000)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        underTest.save(post3);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllBySoldAndPostingDateIsAfterOrderByPromotionDescRatingDesc(false,
                LocalDate.now().minusDays(10));
        Assertions.assertEquals(check.size(), 3);
        Assertions.assertEquals(check.get(0), post2);
        Assertions.assertEquals(check.get(1), post3);
        Assertions.assertEquals(check.get(2), post1);
    }

    @Test
    void shouldFindAllByPostingDateIsBefore() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Test").description("Test")
                .user(us).rating(0).price(100).sold(false).promotion(2222)
                .postingDate(LocalDate.now().minusDays(3)).category(ESTATE).build();
        Post post2 = Post.builder().title("Test").description("Test")
                .user(us).rating(5).price(200).sold(false).promotion(1000)
                .postingDate(LocalDate.now().plusDays(6)).category(ESTATE).build();
        Post post3 = Post.builder().title("Test").description("Test")
                .user(us).rating(8).price(160).sold(false).promotion(1000)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        underTest.save(post3);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllBySoldAndPostingDateIsBeforeOrderByPromotionDescRatingDesc(false,
                LocalDate.now().plusDays(8));
        Assertions.assertEquals(check.size(), 3);
        Assertions.assertEquals(check.get(0), post1);
        Assertions.assertEquals(check.get(1), post3);
        Assertions.assertEquals(check.get(2), post2);
    }

    @Test
    void shouldFindAllByTitleContaining() {
        User us = User.builder().username("mail@mail.ru").password("pass")
                .rating(2).build();
        userRepository.save(us);
        Post post1 = Post.builder().title("Testing").description("Test")
                .user(us).rating(0).price(100).sold(false).promotion(222)
                .postingDate(LocalDate.now().minusDays(3)).category(ESTATE).build();
        Post post2 = Post.builder().title("t est").description("Test")
                .user(us).rating(5).price(200).sold(false).promotion(1000)
                .postingDate(LocalDate.now().plusDays(6)).category(ESTATE).build();
        Post post3 = Post.builder().title("tESt_title").description("Test")
                .user(us).rating(8).price(160).sold(false).promotion(1000)
                .postingDate(LocalDate.now()).category(ESTATE).build();
        Assertions.assertNull(post1.getId());
        underTest.save(post1);
        underTest.save(post2);
        underTest.save(post3);
        Assertions.assertNotNull(post1.getId());

        List<Post> check = underTest.findAllBySoldAndTitleContainingIgnoreCaseOrderByPromotionDescRatingDesc(
                false, "test"
        );
        Assertions.assertEquals(check.size(), 2);
        Assertions.assertEquals(check.get(0), post3);
        Assertions.assertEquals(check.get(1), post1);
    }
}
