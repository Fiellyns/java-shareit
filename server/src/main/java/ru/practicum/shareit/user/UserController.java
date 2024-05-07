package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Поступил POST-запрос в /users");
        UserDto user = userService.create(userDto);
        log.info("POST-запрос /users был обработан: {}", user);
        return user;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") Long userId) {
        log.info("Поступил GET-запрос в /users/{}", userId);
        UserDto user = userService.findById(userId);
        log.info("GET-запрос /users/{} был обработан: {}", userId, user);
        return user;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Поступил GET-запрос в /users");
        Collection<UserDto> userDtos = userService.findAll();
        log.info("GET-запрос /users был обработан: {}", userDtos);
        return userDtos;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable("userId") Long userId) {
        log.info("Поступил PATCH-запрос в /users/{}", userId);
        userDto.setId(userId);
        UserDto user = userService.updateById(userDto);
        log.info("PATCH-запрос /users/{} был обработан: {}", userId, user);
        return user;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        log.debug("Поступил DELETE-запрос в /users/{}/", userId);
        userService.delete(userId);
        log.info("DELETE-запрос /users/{} был обработан", userId);
    }
}
