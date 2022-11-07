package ru.senla.finale.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;
import ru.senla.dto.AuthResponseDTO;
import ru.senla.dto.MessageDTO;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageControllerIT extends BaseControllerIT {

    @BeforeAll
    public void startUp() throws Exception {
        registerUser("user@mail.ru");
        registerUser("user1@mail.ru");
    }

    @Test
    public void shouldSendMessage() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");
        //registerUser("user1@mail.ru");

        mockMvc.perform(post("/messages").param("email", "user1@mail.ru")
                .header(AUTHORIZATION, dto.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("Test message"))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnConversation() throws Exception {
        AuthResponseDTO dto = authenticateUser("user@mail.ru");
        //registerUser("user1@mail.ru");

        mockMvc.perform(post("/messages").param("email", "user1@mail.ru")
                        .header(AUTHORIZATION, dto.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Test message"))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(get("/messages").param("email", "user1@mail.ru")
                        .header(AUTHORIZATION, dto.getToken()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MessageDTO[] conversation = mapper.readValue(response, MessageDTO[].class);

        Assertions.assertNotEquals(0, conversation.length);
        Assertions.assertEquals("Test message", conversation[0].getContent());
    }
}
