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
import ru.senla.model.Category;
import ru.senla.model.Post;
import ru.senla.model.User;
import ru.senla.repository.PostRepository;
import ru.senla.service.PostService;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    private PostService underTest;

    @BeforeEach
    public void setUp() {
        underTest = new PostService(postRepository);
    }

    @Test
    public void create() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        underTest.create(post);

        ArgumentCaptor<Post> captor =
                ArgumentCaptor.forClass(Post.class);

        Mockito.verify(postRepository).save(captor.capture());

        Post captured = captor.getValue();
        Assertions.assertEquals(captured, post);
    }

    @Test
    public void update() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        underTest.update(post);

        ArgumentCaptor<Post> captor =
                ArgumentCaptor.forClass(Post.class);

        Mockito.verify(postRepository).save(captor.capture());

        Post captured = captor.getValue();
        Assertions.assertEquals(captured, post);
    }

    @Test
    public void updatePostSetRatingForUser() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        User user = new User();
        user.setUsername("User");
        post.setUser(user);

        Mockito.when(postRepository.updatePostSetRatingForUser(1, user)).thenReturn(1);

        int check = underTest.updatePostSetRatingForUser(1, user);

        Mockito.verify(postRepository).updatePostSetRatingForUser(1, user);
        Assertions.assertEquals(1, check);
    }

    @Test
    public void findAllByOrderByPromotionDesc() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        Mockito.when(postRepository.findAllBySoldOrderByPromotionDescRatingDesc(false)).thenReturn(List.of(post));

        List<Post> check = underTest.findAllByOrderByPromotionDesc();

        Mockito.verify(postRepository).findAllBySoldOrderByPromotionDescRatingDesc(false);
        Assertions.assertEquals(1, check.size());
    }

    @Test
    public void findAllFilter() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        Mockito.when(postRepository.findAllBySoldOrderByPriceAscPromotionDescRatingDesc(false)).thenReturn(List.of(post));
        Mockito.when(postRepository.findAllBySoldOrderByPostingDateDescPromotionDescRatingDesc(false)).thenReturn(List.of(post));

        List<Post> check = underTest.findAllFilter("price", "asc");
        List<Post> check2 = underTest.findAllFilter("postingDate", "desc");

        Mockito.verify(postRepository).findAllBySoldOrderByPriceAscPromotionDescRatingDesc(false);
        Mockito.verify(postRepository).findAllBySoldOrderByPostingDateDescPromotionDescRatingDesc(false);
        Assertions.assertEquals(1, check.size());
        Assertions.assertEquals(1, check2.size());
    }

    @Test
    public void findAllByCategory() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        Mockito.when(postRepository.findAllByCategoryAndSoldOrderByPromotionDescRatingDesc(Category.WORK, false)).thenReturn(List.of(post));

        List<Post> check = underTest.findAllByCategory(Category.WORK);

        Mockito.verify(postRepository).findAllByCategoryAndSoldOrderByPromotionDescRatingDesc(Category.WORK, false);
        Assertions.assertEquals(1, check.size());
    }

    @Test
    public void findAllByUserAndSold() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");

        User user = new User();
        user.setUsername("User");
        post.setUser(user);

        Mockito.when(postRepository.findAllByUserAndSoldOrderByPromotionDesc(user, false)).thenReturn(List.of(post));

        List<Post> check = underTest.findAllByUserAndSold(user, false);

        Mockito.verify(postRepository).findAllByUserAndSoldOrderByPromotionDesc(user, false);
        Assertions.assertEquals(1, check.size());
    }

    @Test
    public void findAllByPriceLessThan() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");
        post.setPrice(1000);

        Mockito.when(postRepository.findAllBySoldAndPriceLessThanOrderByPromotionDescRatingDesc(false, 1500)).thenReturn(List.of(post));

        List<Post> check = underTest.findAllByPriceLessThan(1500);

        Mockito.verify(postRepository).findAllBySoldAndPriceLessThanOrderByPromotionDescRatingDesc(false, 1500);
        Assertions.assertEquals(1, check.size());
    }

    @Test
    public void findAllByPriceGreaterThan() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");
        post.setPrice(1000);

        Mockito.when(postRepository.findAllBySoldAndPriceGreaterThanOrderByPromotionDescRatingDesc(false, 500)).thenReturn(List.of(post));

        List<Post> check = underTest.findAllByPriceGreaterThan(500);

        Mockito.verify(postRepository).findAllBySoldAndPriceGreaterThanOrderByPromotionDescRatingDesc(false, 500);
        Assertions.assertEquals(1, check.size());
    }

    @Test
    public void findAllByPostingDateLessThan() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");
        post.setPrice(1000);
        post.setPostingDate(LocalDate.of(2022, 9, 10));

        Mockito.when(postRepository.findAllBySoldAndPostingDateIsBeforeOrderByPromotionDescRatingDesc(false, LocalDate.of(2022, 9, 13)))
                .thenReturn(List.of(post));

        List<Post> check = underTest.findAllByPostingDateIsBefore(LocalDate.of(2022, 9, 13));

        Mockito.verify(postRepository).findAllBySoldAndPostingDateIsBeforeOrderByPromotionDescRatingDesc(false, LocalDate.of(2022, 9, 13));
        Assertions.assertEquals(1, check.size());
    }

    @Test
    public void findAllByPostingDateGreaterThan() throws EmptyResponseException {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test");
        post.setPrice(1000);
        post.setPostingDate(LocalDate.of(2022, 9, 10));

        Mockito.when(postRepository.findAllBySoldAndPostingDateIsAfterOrderByPromotionDescRatingDesc(false, LocalDate.of(2022, 9, 3)))
                .thenReturn(List.of(post));

        List<Post> check = underTest.findAllByPostingDateIsAfter(LocalDate.of(2022, 9, 3));

        Mockito.verify(postRepository).findAllBySoldAndPostingDateIsAfterOrderByPromotionDescRatingDesc(false, LocalDate.of(2022, 9, 3));
        Assertions.assertEquals(1, check.size());
    }
}
