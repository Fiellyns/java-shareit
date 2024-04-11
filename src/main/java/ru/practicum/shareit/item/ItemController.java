package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.forDto.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Поступил POST-запрос в /items");
        ItemDto item = itemService.create(itemDto, userId);
        log.info("POST-запрос /items был обработан: {}", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long itemId) {
        log.info("Поступил PATCH-запрос в /items/{}", itemId);
        itemDto.setId(itemId);
        ItemDto item = itemService.update(userId, itemDto);
        log.info("PATCH-запрос /items/{} был обработан: {}", itemId, item);
        return item;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        log.info("Поступил GET-запрос в /items/{}", itemId);
        ItemDto item = itemService.getByItemIdAndUserId(userId, itemId);
        log.info("GET-запрос /items/{} был обработан: {}", itemId, item);
        return item;
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET-запрос в /items/ на получение вещей владельца с ID: {}", userId);
        Collection<ItemDto> itemDtos = itemService.getItemsByOwner(userId);
        log.info("GET-запрос /items/ на получение вещей владельца с ID: {} был обработан: {}", userId, itemDtos);
        return itemDtos;
    }

    @GetMapping("/search")
    public Collection<ItemDto> getByText(@RequestParam(name = "text") String text) {
        log.info("Поступил GET-запрос в /items/search/{}", text);
        Collection<ItemDto> itemDtos = itemService.findAllByText(text);
        log.info("GET-запрос /items/search/{} был обработан: {}", text, itemDtos);
        return itemDtos;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long itemId,
                             @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Поступил POST-запрос в /items/{}/comment", itemId);
        CommentDto createdComment = itemService.create(commentDto, userId, itemId);
        log.info("POST-запрос /items/{}/comment был обработан: {}", itemId, createdComment);
        return createdComment;
    }

}