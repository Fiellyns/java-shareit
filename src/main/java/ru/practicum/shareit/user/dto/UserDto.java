package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {UserDtoCreate.class})
    private String name;
    @Email(groups = {UserDtoCreate.class, UserDtoUpdate.class}, message = "Неверный формат адреса электронной почты")
    @NotBlank(groups = {UserDtoCreate.class})
    private String email;
}
