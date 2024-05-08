package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                new ArrayList<>());
    }

    public ItemDto toDto(Item item, List<CommentDto> commentDto) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                commentDto);
    }

    public ItemDto toDto(Item item, BookingInfoDto lastBooking, BookingInfoDto nextBooking, List<CommentDto> commentDto) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                item.getRequest() != null ? item.getRequest().getId() : null,
                commentDto);
    }

    public Item toModel(ItemDto itemDto, User owner) {
        return new Item().toBuilder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public Item toModel(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item().toBuilder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemRequest)
                .build();
    }
}
