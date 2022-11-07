package ru.senla.finale.integration.controller;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;
import ru.senla.dto.AuthResponseDTO;
import ru.senla.dto.CreatePostDTO;
import ru.senla.dto.UpdatePostDTO;
import ru.senla.model.Category;

import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostControllerIT extends BaseControllerIT {

    protected AuthResponseDTO adminDto;

    @BeforeAll
    public void startUp() throws Exception {
        adminDto = registerAdmin();

        createPostsAndComments("user@mail.ru", "user1@mail.ru");
    }

    @Test
    public void shouldReturnAllPosts() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");

        String response = mockMvc.perform(get("/posts")
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] posts = mapper.readValue(response, Map[].class);

        Assertions.assertEquals(3, posts.length);
        Assertions.assertEquals(postRepository.findAll().size(), posts.length);
        Assertions.assertEquals("Test 2", posts[1].get("title"));
    }

    @Test
    public void shouldCreateNewPost() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");

        String content = mapper.writeValueAsString(CreatePostDTO.builder().title("Test 4")
                .description("T4").price(250).category(Category.ESTATE).build());

        mockMvc.perform(post("/posts")
                        .header(AUTHORIZATION, dto.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(content().string("Ваше объявление создано"));

        Assertions.assertEquals(4, postRepository.findAll().size());
        Assertions.assertEquals("Test 4", postRepository.findAll().get(3).getTitle());
    }

    @Test
    public void shouldEditPostAndRejectEditFromWrongUser() throws Exception {
        AuthResponseDTO dto = authenticateUser("user1@mail.ru");

        String content = mapper.writeValueAsString(UpdatePostDTO.builder()
                .title("Test 3.1").build());

        mockMvc.perform(patch("/posts/{id}", 3L)
                        .header(AUTHORIZATION, dto.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string("Данные объявления обновлены"));

        Assertions.assertEquals("Test 3.1", postRepository.findAll().get(2).getTitle());

        mockMvc.perform(patch("/posts/{id}", 2L)
                        .header(AUTHORIZATION, dto.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(content().string("Вы не можете изменить данное объявление"));
    }

    @Test
    public void shouldDeletePostAndRejectDeletionFromWrongUser() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");

        mockMvc.perform(delete("/posts/{id}", 2L)
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Объявление удалено"));

        Assertions.assertEquals(2, postRepository.findAll().size());
        Assertions.assertEquals("Test 3", postRepository.findAll().get(1).getTitle());

        mockMvc.perform(delete("/posts/{id}", 3L)
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Вы не можете удалить данное объявление"));
    }

    @Test
    public void shouldPromotePostAndRejectPromotionFromWrongUser() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");

        mockMvc.perform(post("/posts/{id}/promote", 1L)
                        .param("promotion", String.valueOf(100))
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Вы проплатили отображение объявления в топе выдачи"));

        Assertions.assertEquals(150, postRepository.findAll().get(0).getPromotion());

        mockMvc.perform(post("/posts/{id}/promote", 3L)
                        .param("promotion", String.valueOf(100))
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Вы не можете проплатить отображение данного объявления"));
    }

    @Test
    public void shouldBuyPostAndRejectPurchaseFromOwner() throws Exception {
        AuthResponseDTO dto = authenticateUser("user1@mail.ru");

        mockMvc.perform(post("/posts/buy/{id}", 2L).param("grade", String.valueOf(-3))
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Объявление куплено"));

        Assertions.assertEquals(3, postRepository.findAll().size());
        Assertions.assertTrue(postRepository.findAll().get(1).isSold());

        mockMvc.perform(post("/posts/buy/{id}", 3L).param("grade", String.valueOf(-3))
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Вы не можете купить данное объявление"));
    }

    @Test
    public void shouldFindPostsByTitle() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");

        String response = mockMvc.perform(get("/posts/search").param("title", "Test")
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] posts = mapper.readValue(response, Map[].class);

        Assertions.assertEquals(3, posts.length);
        Assertions.assertEquals(postRepository.findAll().size(), posts.length);
        Assertions.assertEquals("Test 2", posts[1].get("title"));
    }

    @Test
    public void shouldFindPostsByUser() throws Exception {
        AuthResponseDTO dto = authenticateUser("user1@mail.ru");

        String response = mockMvc.perform(get("/posts/by_user/available")
                        .param("email", "user@mail.ru")
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] posts = mapper.readValue(response, Map[].class);

        Assertions.assertEquals(2, posts.length);
        Assertions.assertEquals("user@mail.ru", posts[0].get("userEmail"));
    }

    @Test
    public void shouldFindPostsByCategory() throws Exception {
        AuthResponseDTO dto = authenticateUser("user1@mail.ru");

        String response = mockMvc.perform(get("/posts/category")
                        .param("category", "ESTATE")
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] posts = mapper.readValue(response, Map[].class);

        Assertions.assertEquals(2, posts.length);
        Assertions.assertEquals("Test 1", posts[1].get("title"));
        Assertions.assertEquals("Test 3", posts[0].get("title"));
    }

    @Test
    public void shouldReturnCommentsByPost() throws Exception {
        AuthResponseDTO dto = authenticateUser("user1@mail.ru");

        String response = mockMvc.perform(get("/posts/{id}/comments", 1L)
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, String>[] comments = mapper.readValue(response, Map[].class);

        Assertions.assertEquals(1, comments.length);
        Assertions.assertEquals(commentRepository.findAll().size(), comments.length);
        Assertions.assertEquals("Test comment", comments[0].get("content"));
    }

    @Test
    public void shouldAddCommentToPost() throws Exception {
        AuthResponseDTO dto = authenticateUser("user1@mail.ru");

        mockMvc.perform(post("/posts/{id}/comments", 1L)
                        .header(AUTHORIZATION, dto.getToken())
                        .content("Test comment 2"))
                .andExpect(status().isCreated());

        Assertions.assertEquals(2, commentRepository.findAll().size());
        Assertions.assertEquals("Test comment 2", commentRepository.findAll().get(1).getContent());
    }
}
