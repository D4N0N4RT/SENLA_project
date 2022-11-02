package ru.senla.finale.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.senla.controller.AdminController;
import ru.senla.model.Comment;
import ru.senla.model.Message;
import ru.senla.model.Post;
import ru.senla.model.Role;
import ru.senla.model.User;
import ru.senla.repository.CommentRepository;
import ru.senla.repository.MessageRepository;
import ru.senla.repository.PostRepository;
import ru.senla.repository.UserRepository;
import ru.senla.security.JwtConfigurer;
import ru.senla.security.JwtTokenProvider;
import ru.senla.service.CommentService;
import ru.senla.service.PostService;
import ru.senla.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser("user1")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @MockBean
    PostService postService;

    @MockBean
    UserService userService;

    @MockBean
    JwtTokenProvider provider;

    @MockBean
    JwtConfigurer configurer;

    @MockBean
    PasswordEncoder encoder;

    @MockBean
    AuthenticationManager manager;

    @MockBean
    MessageRepository messageRepository;

    @MockBean
    CommentRepository repo;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PostRepository postRepository;

    private List<User> users;

    private User us1;
    private User us2;

    @BeforeEach
    void setUp() {
        this.us1 = User.builder().id(1L).username("user1").password("pass")
                .role(Role.USER).build();
        this.us2 = User.builder().id(2L).username("user2").password("pass")
                .role(Role.USER).build();
        this.users = new ArrayList<>();
        this.users.add(us1);
        this.users.add(us2);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        given(userService.getAll())
                .willReturn(users);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc.perform(get("/admin/users"))
                .andExpect(content().string(objectMapper.writeValueAsString(users)))
                .andExpect(status().isFound());
    }

    @Test
    public void testDeleteUser() throws Exception {
        given(userService.loadUserByUsername("user1"))
                .willReturn(users.get(0));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc.perform(delete("/admin/users").param("email", "user1"))
                .andExpect(content().string("Пользователь удален"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletePost() throws Exception {
        given(postService.findById(1L))
                .willReturn(Optional.of(Post.builder().id(1L).title("Test").build()));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc.perform(delete("/admin/posts/{id}", 1))
                .andExpect(content().string("Объявление удалено"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteComment() throws Exception {
        given(commentService.findById(1L))
                .willReturn(Optional.of(Comment.builder().id(1L).content("Test").build()));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc.perform(delete("/admin/comments/{id}", 1))
                .andExpect(content().string("Комментарий удален"))
                .andExpect(status().isOk());
    }
}
