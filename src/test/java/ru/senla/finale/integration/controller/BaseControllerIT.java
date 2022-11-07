package ru.senla.finale.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.senla.dto.AuthDTO;
import ru.senla.dto.AuthResponseDTO;
import ru.senla.dto.UserDTO;
import ru.senla.model.Category;
import ru.senla.model.Comment;
import ru.senla.model.Post;
import ru.senla.model.Role;
import ru.senla.model.User;
import ru.senla.repository.CommentRepository;
import ru.senla.repository.PostRepository;
import ru.senla.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class BaseControllerIT {

    protected final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired protected MockMvc mockMvc;
    @Autowired protected PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected PostRepository postRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(userRepository);
    }

    protected AuthResponseDTO registerUser(String username) throws Exception {
        String userDto = mapper.writeValueAsString(
                UserDTO.builder().username(username).password("passW0rd")
                        .name("Name").surname("surname")
                        .phone("0123456789").build()
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDto))
                .andExpect(status().isOk());
        return authenticateUser(username);
    }

    protected AuthResponseDTO authenticateUser(String username) throws Exception {
        String authDto = mapper.writeValueAsString(
                AuthDTO.builder().username(username).password("passW0rd").build()
        );
        String response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authDto))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthResponseDTO dto = mapper.readValue(response, AuthResponseDTO.class);

        Assertions.assertFalse(dto.getToken().isEmpty());
        return dto;
    }

    protected AuthResponseDTO registerAdmin() throws Exception {
        userRepository.save(User.builder().username("admin").password(encoder.encode("passW0rd"))
                .role(Role.ADMIN).isActive(true).build());
        return authenticateAdmin();
    }

    protected AuthResponseDTO authenticateAdmin() throws Exception {
        String authDto = mapper.writeValueAsString(
                AuthDTO.builder().username("admin").password("passW0rd").build()
        );
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authDto))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthResponseDTO dto = mapper.readValue(response, AuthResponseDTO.class);

        Assertions.assertFalse(dto.getToken().isEmpty());
        return dto;
    }

    protected void createPostsAndComments(String username1, String username2) throws Exception {
        registerUser(username1);
        registerUser(username2);

        User us1 = userRepository.findById(2L).get();
        User us2 = userRepository.findById(3L).get();

        Post post1 = Post.builder().id(1L).title("Test 1").description("T1")
                .category(Category.ESTATE).price(1000).sold(false).rating(10).promotion(50)
                .postingDate(LocalDate.now().plusDays(5)).user(us1).build();
        Post post2 = Post.builder().id(2L).title("Test 2").description("T2")
                .category(Category.TRANSPORT).price(500).sold(false).rating(10).promotion(100)
                .postingDate(LocalDate.now().plusDays(3)).user(us1).build();
        Post post3 = Post.builder().id(3L).title("Test 3").description("T3")
                .category(Category.ESTATE).price(750).sold(false).rating(5).promotion(150)
                .postingDate(LocalDate.now().minusDays(2)).user(us2).build();

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        commentRepository.save(Comment.builder().content("Test comment").user(us2)
                .post(post1).time(LocalDateTime.now()).build());
    }
}
