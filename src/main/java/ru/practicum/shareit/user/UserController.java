package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoCreate;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@Validated({UserDtoCreate.class}) @RequestBody UserDto userDto) {
        log.info("Поступил POST-запрос в /users");
        return userService.create(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") Long userId) {
        log.info("Поступил GET-запрос в /users/{}", userId);
        return userService.findById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Поступил GET-запрос в /users");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated({UserDtoUpdate.class}) @RequestBody UserDto userDto,
                          @PathVariable("userId") Long userId) {
        log.info("Поступил Patch-запрос в /users/{}", userId);
        userDto.setId(userId);
        return userService.updateById(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        log.debug("Поступил DELETE-запрос в /users/{}/", userId);
        userService.delete(userId);
    }
}
