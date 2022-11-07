package ru.senla.finale.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.senla.dto.AuthResponseDTO;

import java.util.Map;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminControllerIT extends BaseControllerIT {

    protected AuthResponseDTO adminDto;

    @BeforeAll
    public void startUp() throws Exception {
        adminDto = registerAdmin();

        createPostsAndComments("user@mail.ru", "user1@mail.ru");
    }

    @Test
    public void shouldReturnListOfUsers() throws Exception {

        //registerUser("user@mail.ru");

        String response = mockMvc.perform(get("/admin/users")
                .header(AUTHORIZATION, adminDto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] users = mapper.readValue(response, Map[].class);

        Assertions.assertNotEquals(0, users.length);
        Assertions.assertEquals("user@mail.ru", users[1].get("username"));
        Assertions.assertEquals("user1@mail.ru", users[2].get("username"));
    }

    @Test
    public void shouldDeleteUser() throws Exception {

        registerUser("user2@mail.ru");

        mockMvc.perform(delete("/admin/users").param("email", "user2@mail.ru")
                        .header(AUTHORIZATION, adminDto.getToken()))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get("/admin/users")
                        .header(AUTHORIZATION, adminDto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] users = mapper.readValue(response, Map[].class);

        Assertions.assertEquals(3, users.length);
        Assertions.assertEquals("admin", users[0].get("username"));
    }

    @Test
    public void shouldDeletePost() throws Exception {
        mockMvc.perform(delete("/admin/posts/{id}", 2L).param("email", "user2@mail.ru")
                        .header(AUTHORIZATION, adminDto.getToken()))
                .andExpect(status().isOk());

        Assertions.assertEquals(2, postRepository.findAll().size());
    }

    @Test
    public void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/admin/comments/{id}", 1L).param("email", "user2@mail.ru")
                        .header(AUTHORIZATION, adminDto.getToken()))
                .andExpect(status().isOk());

        Assertions.assertEquals(0, commentRepository.findAll().size());
    }
}
