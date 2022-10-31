package ru.senla.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senla.dto.CommentDTO;
import ru.senla.dto.CreatePostDTO;
import ru.senla.dto.GetPostDTO;
import ru.senla.dto.UpdatePostDTO;
import ru.senla.exception.EmptyResponseException;
import ru.senla.exception.PriceValidException;
import ru.senla.exception.WrongAuthorityException;
import ru.senla.exception.WrongIdException;
import ru.senla.mapper.PostMapper;
import ru.senla.model.Category;
import ru.senla.model.Comment;
import ru.senla.model.Post;
import ru.senla.model.User;
import ru.senla.security.JwtTokenProvider;
import ru.senla.service.CommentService;
import ru.senla.service.PostService;
import ru.senla.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PostMapper postMapper;

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService,
                          JwtTokenProvider jwtTokenProvider, PostMapper postMapper) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.postMapper = postMapper;
    }

    @GetMapping("")
    public ResponseEntity<?> getAll() throws EmptyResponseException {
        List<Post> posts = postService.findAllByOrderByPromotionDesc();
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/by_user/sold")
    public ResponseEntity<?> getSoldPostsByUser(@RequestParam(name="email") @NotBlank String email) throws EmptyResponseException {
        User user = (User) userService.loadUserByUsername(email);
        List<Post> posts = postService.findAllByUserAndSold(user, true);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/by_user/available")
    public ResponseEntity<?> getAvailablePostsByUser(@RequestParam(name="email") @NotBlank String email) throws EmptyResponseException {
        User user = (User) userService.loadUserByUsername(email);
        List<Post> posts = postService.findAllByUserAndSold(user, false);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getAllByCategory(@PathVariable(name="category") @NotNull Category category) throws EmptyResponseException {
        List<Post> posts = postService.findAllByCategory(category);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/sort/{field}/{order}")
    public ResponseEntity<?> getAllSort(@PathVariable(name="field") @NotBlank String field,
                                          @PathVariable(name="order") @NotBlank String order) throws EmptyResponseException {
        List<Post> posts = postService.findAllFilter(field, order);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/filter/price/{option}")
    public ResponseEntity<?> getAllFilterPrice(@RequestParam(name="price") double price,
                                        @PathVariable(name="option") @NotBlank String option) throws EmptyResponseException {
        List<Post> posts;
        if (Objects.equals(option, "less"))
            posts = postService.findAllByPriceLessThan(price);
        else
            posts = postService.findAllByPriceGreaterThan(price);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/filter/date/{option}")
    public ResponseEntity<?> getAllFilterDate(@RequestParam(name="date") LocalDate date,
                                              @PathVariable(name="option") String option) throws EmptyResponseException {
        List<Post> posts;
        if (Objects.equals(option, "before"))
            posts = postService.findAllByPostingDateIsBefore(date);
        else
            posts = postService.findAllByPostingDateIsAfter(date);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByTitle(@RequestParam @NotBlank String title) throws EmptyResponseException {
        List<Post> posts = postService.findAllByTitleContainingIgnoreCase(title);
        List<GetPostDTO> dtos = posts.stream().map(Post::fromPost).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(name="id") long id) throws WrongIdException {
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        GetPostDTO dto = post.fromPost();
        return new ResponseEntity<>(dto, HttpStatus.FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<?> createPost(@RequestBody @Valid CreatePostDTO dto, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        Post post = dto.toPost(user);
        postService.create(post);
        return new ResponseEntity<>("Ваше объявление создано", HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable(name="id") long id, HttpServletRequest request)
            throws WrongIdException, WrongAuthorityException {
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        if (Objects.equals(post.getUser().getUsername(), user.getUsername())) {
            postService.delete(post);
            return new ResponseEntity<>("Объявление удалено", HttpStatus.OK);
        } else {
            throw new WrongAuthorityException("Вы не можете удалить данное объявление");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editPost(@PathVariable(name="id") long id, @RequestBody @Valid UpdatePostDTO dto, HttpServletRequest request)
            throws WrongIdException, WrongAuthorityException, PriceValidException {
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        if (Objects.equals(post.getUser().getUsername(), user.getUsername())) {
            if (dto.getPrice() < 1)
                throw new PriceValidException("Ошибка валидации, проверьте введенные данные\nОшибка: Цена должна быть не меньше 1");
            postMapper.updatePostFromDto(dto, post);
            postService.update(post);
            return new ResponseEntity<>("Данные объявления обновлены", HttpStatus.OK);
        } else {
            throw new WrongAuthorityException("Вы не можете изменить данное объявление");
        }
    }

    @PostMapping("/buy/{id}")
    public ResponseEntity<?> buyPost(@PathVariable(name="id") long id, @RequestParam int grade, HttpServletRequest request)
            throws WrongIdException, WrongAuthorityException {
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        if (!Objects.equals(post.getUser().getUsername(), user.getUsername())) {
            post.setSold(true);
            postService.update(post);
            User seller = post.getUser();
            seller.setRating(seller.getRating() + grade);
            userService.update(seller);
            postService.updatePostSetRatingForUser(seller.getRating(), seller);
            return new ResponseEntity<>("Объявление куплено", HttpStatus.OK);
        } else {
            throw new WrongAuthorityException("Вы не можете изменить данное объявление");
        }
    }

    @PostMapping("/{id}/promote")
    public ResponseEntity<?> promotePost(@PathVariable(name="id") long id, HttpServletRequest request,
                                         @RequestParam(name="promotion") double promotion)
            throws WrongIdException, WrongAuthorityException {
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        if (Objects.equals(post.getUser().getUsername(), user.getUsername())) {
            post.setPromotion(promotion);
            postService.update(post);
            return new ResponseEntity<>("Вы проплатили отображение объявления в топе выдачи", HttpStatus.OK);
        } else {
            throw new WrongAuthorityException("Вы не можете проплатить отображение данного объявления");
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable(name="id") long id)
            throws WrongIdException, EmptyResponseException {
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        List<Comment> comments = commentService.findAllByPost(post);
        List<CommentDTO> dtos = comments.stream().map(Comment::toDTO).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.FOUND);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable(name="id") long id,
                                        @RequestBody @Valid @NotBlank(message = "Комментарий не может быть пустым") String text,
                                        HttpServletRequest request) throws WrongIdException {
        String token = jwtTokenProvider.resolveToken(request);
        String username = jwtTokenProvider.getUsername(token);
        User user = (User) userService.loadUserByUsername(username);
        Post post = postService.findById(id).orElseThrow(() -> new WrongIdException("Неправльный id"));
        Comment comment = Comment.builder().post(post).user(user).content(text)
                .time(LocalDateTime.now()).build();
        commentService.create(comment);
        return new ResponseEntity<>("Ваш комментарий добавлен", HttpStatus.CREATED);
    }
}
