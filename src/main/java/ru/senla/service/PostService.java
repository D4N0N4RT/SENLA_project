package ru.senla.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.exception.EmptyResponseException;
import ru.senla.model.Category;
import ru.senla.model.Post;
import ru.senla.model.User;
import ru.senla.repository.PostRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Post> findById(long id) {
        log.info("Find post by id = {}", id);
        return postRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByOrderByPromotionDesc() throws EmptyResponseException {
        log.info("Find all posts w/o order or filter");
        List<Post> posts = postRepository.findAllBySoldOrderByPromotionDescRatingDesc(false);
        if (posts.isEmpty())
            throw new EmptyResponseException("На данный момент нет ни одного размещенного объявления");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllFilter(String field, String option) throws EmptyResponseException {
        log.info("Find all posts with filter by {}", field);
        List<Post> posts;
        if (Objects.equals(field, "price") && Objects.equals(option, "asc"))
            posts = postRepository.findAllBySoldOrderByPriceAscPromotionDescRatingDesc(false);
        else if (Objects.equals(field, "price") && Objects.equals(option, "desc"))
            posts = postRepository.findAllBySoldOrderByPriceDescPromotionDescRatingDesc(false);
        else if (Objects.equals(field, "posting date") && Objects.equals(option, "asc"))
           posts = postRepository.findAllBySoldOrderByPostingDateAscPromotionDescRatingDesc(false);
        else
            posts = postRepository.findAllBySoldOrderByPostingDateDescPromotionDescRatingDesc(false);
        if (posts.isEmpty())
            throw new EmptyResponseException("На данный момент нет ни одного размещенного объявления");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByCategory(Category category) throws EmptyResponseException {
        log.info("Find all posts with certain category {}", category.name());
        List<Post> posts = postRepository.findAllByCategoryAndSoldOrderByPromotionDescRatingDesc(category, false);
        if (posts.isEmpty())
            throw new EmptyResponseException("Список объявлений по данной категории пуст");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByUserAndSold(User user, boolean sold) throws EmptyResponseException {
        log.info("Find all posts by user {} and which {} sold", user.getUsername(), sold ? "are" : "are not");
        List<Post> posts = postRepository.findAllByUserAndSoldOrderByPromotionDesc(user, sold);
        if (posts.isEmpty() && !sold)
            throw new EmptyResponseException("У данного пользователя нет выставленных объявлений на данный момент");
        else if (posts.isEmpty())
            throw new EmptyResponseException("История продаж данного пользователя пуста");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByPriceLessThan(double price) throws EmptyResponseException {
        log.info("Find all posts with price less than {}", price);
        List<Post> posts = postRepository.findAllBySoldAndPriceLessThanOrderByPromotionDescRatingDesc(false, price);
        if (posts.isEmpty())
            throw new EmptyResponseException("Объявления, стоящие дешевле " + price + " отсутствуют");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByPriceGreaterThan(double price) throws EmptyResponseException {
        log.info("Find all posts with price greater than {}", price);
        List<Post> posts = postRepository.findAllBySoldAndPriceGreaterThanOrderByPromotionDescRatingDesc(false, price);
        if (posts.isEmpty())
            throw new EmptyResponseException("Объявления, стоящие дороже " + price + " отсутствуют");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByPostingDateIsBefore(LocalDate postingDate) throws EmptyResponseException {
        log.info("Find all posts with posting date is before {}", postingDate);
        List<Post> posts = postRepository.findAllBySoldAndPostingDateIsBeforeOrderByPromotionDescRatingDesc(false, postingDate);
        if (posts.isEmpty())
            throw new EmptyResponseException("Объявления, размещенные раньше " + postingDate.toString() + " отсутствуют");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByPostingDateIsAfter(LocalDate postingDate) throws EmptyResponseException {
        log.info("Find all posts with posting date is after {}", postingDate);
        List<Post> posts = postRepository.findAllBySoldAndPostingDateIsAfterOrderByPromotionDescRatingDesc(false, postingDate);
        if (posts.isEmpty())
            throw new EmptyResponseException("Объявления, размещенные позже " + postingDate.toString() + " отсутствуют");
        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> findAllByTitleContainingIgnoreCase(String title) throws EmptyResponseException {
        log.info("Find all by title containing string '{}'", title);
        List<Post> posts = postRepository.findAllBySoldAndTitleContainingIgnoreCaseOrderByPromotionDescRatingDesc(false, title);
        if (posts.isEmpty())
            throw new EmptyResponseException("Объявления с таким названием отсутствуют");
        return posts;
    }

    @Transactional
    public int updatePostSetRatingForUser(Integer rating, User user) {
        log.info("Update rating of posts from user - {}", user.getUsername());
        return postRepository.updatePostSetRatingForUser(rating, user);
    }

    @Transactional
    public void create(Post post) {
        log.info("Create new post");
        postRepository.save(post);
    }

    @Transactional
    public void delete(Post post) {
        log.info("Delete post with id {}", post.getId());
        postRepository.delete(post);
    }

    @Transactional
    public void update(Post post) {
        log.info("Update post with id {}", post.getId());
        postRepository.save(post);
    }
}
