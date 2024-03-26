package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
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

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Поступил POST-запрос в /items");
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long itemId) {
        log.info("Поступил Patch-запрос в /items/{}", itemId);
        itemDto.setId(itemId);
        return itemService.update(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable("itemId") Long itemId) {
        log.info("Поступил GET-запрос в /items/{}", itemId);
        return itemService.findById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поступил GET-запрос в /items/ на получение вещей владельца с ID: {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getByText(@RequestParam(name = "text") String text) {
        log.info("Поступил GET-запрос в /items/search/{}", text);
        return itemService.findAllByText(text);
    }

}