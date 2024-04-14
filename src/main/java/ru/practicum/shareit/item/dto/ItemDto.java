package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.forDto.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    @Size(max = 200)
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    BookingInfoDto lastBooking;
    BookingInfoDto nextBooking;
    List<CommentDto> comments;
}
