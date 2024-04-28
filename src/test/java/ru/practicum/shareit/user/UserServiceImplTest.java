package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @InjectMocks
    private UserServiceImpl userService;
    private User user1;
    private UserDto userCreateDto;
    private UserDto userUpdateDto;
    private UserDto userDtoForUser1;

    @BeforeEach
    public void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        userCreateDto = UserDto.builder()
                .name("name")
                .email("name@email.com")
                .build();
        userUpdateDto = UserDto.builder()
                .email("newname@email.com")
                .build();
        userDtoForUser1 = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
    }

    @AfterEach
    public void clean() {
        user1 = null;
        userCreateDto = null;
        userUpdateDto = null;
        userDtoForUser1 = null;
    }


    @Test
    void getAll_whenAllUsersFound_thenReturnUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1));

        Collection<UserDto> result = userService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(List.of(userDtoForUser1));
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        UserDto result = userService.findById(user1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(userDtoForUser1);
    }

    @Test
    void getUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.findById(userId));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));
    }

    @Test
    void create_whenUserValid_thenReturnUserDto() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserDto savedUser = userService.create(userCreateDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(userDtoForUser1);
    }

    @Test
    void update_whenUpdateOnlyEmail_thenReturnUserDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        user1.setEmail(userUpdateDto.getEmail());
        userDtoForUser1.setEmail(userUpdateDto.getEmail());
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        userUpdateDto.setId(user1.getId());
        UserDto updatedUser = userService.updateById(userUpdateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(userDtoForUser1);
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        userUpdateDto.setId(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.updateById(userUpdateDto));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));
    }

    @Test
    void delete() {
        Long userId = 1L;
        willDoNothing().given(userRepository).deleteById(any());

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
