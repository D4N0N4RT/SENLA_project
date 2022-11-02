package ru.senla.finale.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.senla.controller.MessageController;
import ru.senla.model.Message;
import ru.senla.model.Role;
import ru.senla.model.User;
import ru.senla.repository.CommentRepository;
import ru.senla.repository.MessageRepository;
import ru.senla.repository.PostRepository;
import ru.senla.repository.UserRepository;
import ru.senla.security.JwtConfigurer;
import ru.senla.security.JwtTokenProvider;
import ru.senla.service.MessageService;
import ru.senla.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@WebMvcTest(controllers = MessageController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser("user1")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MessageService messageService;

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

    /*@MockBean
    SecurityConfig config;*/

    private List<Message> messages;

    private User us1;
    private User us2;

    @BeforeEach
    void setUp() {
        this.us1 = User.builder().id(1L).username("user1").password("pass")
                .role(Role.USER).build();
        this.us2 = User.builder().id(2L).username("user2").password("pass")
                .role(Role.USER).build();
        this.messages = new ArrayList<>();
        this.messages.add(Message.builder().id(1L).sender(us1).receiver(us2)
                .content("Message 1").time(LocalDateTime.now().minusMinutes(10))
                .build());
        this.messages.add(Message.builder().id(2L).sender(us2).receiver(us1)
                .content("Message 2").time(LocalDateTime.now().minusMinutes(7))
                .build());
        this.messages.add(Message.builder().id(3L).sender(us2).receiver(us1)
                .content("Message 3").time(LocalDateTime.now().minusMinutes(6))
                .build());
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSendMessage() throws Exception {
        //given(messageService.create(any(Message.class)))
                //.willAnswer((invocation) -> invocation.getArgument(0));

        Message message = Message.builder().id(4L).sender(us1).receiver(us2)
                .content("Message 4").time(LocalDateTime.now())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc.perform(post("/messages").param("email", "user2")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(content().string("Ваше сообщение отправлено"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetConversation() throws Exception {
        given(messageService.getConversation(any(User.class), any(User.class)))
                .willReturn(messages);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc.perform(get("/messages").param("email", "user2"))
                .andExpect(content().string(objectMapper.writeValueAsString(messages)))
                .andExpect(status().isFound());
    }
}
