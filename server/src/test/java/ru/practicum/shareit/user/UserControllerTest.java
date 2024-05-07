package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;


    @Autowired
    private MockMvc mvc;

    private UserDto userCreateDto;
    private UserDto userDto;
    private UserDto userUpdatedDto;
    private UserDto userDtoToUpdate;

    private List<UserDto> users;

    @BeforeEach
    void setUp() {
        userCreateDto = UserDto.builder()
                .name("name")
                .email("name@email.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@email.ru")
                .build();

        userDtoToUpdate = UserDto.builder()
                .name("updateName")
                .email("updateName@email.ru")
                .build();

        userUpdatedDto = UserDto.builder()
                .id(1L)
                .name("updateName")
                .email("updateName@email.ru")
                .build();

        users = new ArrayList<>();
    }

    @AfterEach
    void clean() {
        userDtoToUpdate = null;
        userDto = null;
        userCreateDto = null;
        users = null;
    }

    @Test
    void findAll_whenArgsIsValid_thenStatusIsOkAndReturnUsersList() throws Exception {
        users.add(userDto);

        when(userService.findAll())
                .thenReturn(users);

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(users));
    }

    @Test
    void findById_whenUserIsFound_thenStatusIsOkAndReturnUserDto() throws Exception {
        when(userService.findById(anyLong()))
                .thenReturn(userDto);

        String result = mvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(userDto));
    }

    @Test
    void findById_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/users/{id}", userDto.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_whenUserIsValid_thenStatusIsCreatedAndReturnSavedUser() throws Exception {
        when(userService.create(any(UserDto.class)))
                .thenReturn(userDto);

        String result = mvc.perform(post("/users")
                        .content(String.valueOf(mapper.writeValueAsString(userCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(userDto));
    }

    @Test
    void updateById_whenUserIsValid_thenStatusIsOkAndReturnUpdatedUser() throws Exception {
        when(userService.updateById(any(UserDto.class)))
                .thenReturn(userUpdatedDto);

        String result = mvc.perform(patch("/users/{id}", userDto.getId())
                        .content(String.valueOf(mapper.writeValueAsString(userDtoToUpdate)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(userUpdatedDto));
    }

    @Test
    void delete_whenArgsIsValid_thenStatusIsNoContent() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userDto.getId()))
                .andExpect(status().isNoContent());

        verify(userService).delete(userDto.getId());
    }
}
