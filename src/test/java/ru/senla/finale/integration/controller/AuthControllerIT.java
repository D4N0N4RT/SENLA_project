package ru.senla.finale.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.senla.dto.AuthDTO;
import ru.senla.dto.AuthResponseDTO;
import ru.senla.dto.UserDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerIT extends BaseControllerIT {

    @Test
    public void shouldRegisterAndAuth() throws Exception {
        String userDto = mapper.writeValueAsString(
                UserDTO.builder().username("user4@mail.ru").password("passW0rd")
                        .name("Name").surname("surname")
                        .phone("0123456789").build()
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDto))
                .andExpect(status().isOk());
        String authDto = mapper.writeValueAsString(
                AuthDTO.builder().username("user4@mail.ru").password("passW0rd").build()
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
        Assertions.assertEquals(dto.getUsername(), "user4@mail.ru");
    }

    @Test
    public void shouldNotRegisterWithDuplicateUsername() throws Exception {
        String userDto = mapper.writeValueAsString(
                UserDTO.builder().username("user@mail.ru").password("passW0rd")
                        .name("Name").surname("surname")
                        .phone("0123456789").build()
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDto))
                .andExpect(status().isOk());
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDto))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldNotRegisterWithIncorrectPassword() throws Exception {
        String userDto = mapper.writeValueAsString(
                UserDTO.builder().username("user@mail.ru").password("paswd")
                        .name("Name").surname("surname")
                        .phone("0123456789").build()
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDto))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldNotRegisterWthEmptyBody() throws Exception {
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void shouldNotAuthWithWrongCredentials() throws Exception {
        String userDto = mapper.writeValueAsString(
                UserDTO.builder().username("user3@mail.ru").password("passW0rd")
                        .name("Name").surname("surname")
                        .phone("0123456789").build()
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDto))
                .andExpect(status().isOk());
        String authDto = mapper.writeValueAsString(
                AuthDTO.builder().username("user3@mail.ru").password("passW0rD").build()
        );
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authDto))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Bad credentials"));
    }
}
