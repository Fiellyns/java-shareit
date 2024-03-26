package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemDao itemDao;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemMapper itemMapper, ItemDao itemDao, UserService userService) {
        this.itemMapper = itemMapper;
        this.itemDao = itemDao;
        this.userService = userService;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        userService.existsUserById(userId);
        return itemMapper.toItemDto(itemDao.create(itemMapper.toItem(itemDto, userId)));
    }

    @Override
    public ItemDto findById(Long itemId) {
        existsItemById(itemId);
        return itemMapper.toItemDto(itemDao.findById(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(long userId) {
        userService.existsUserById(userId);
        return itemDao.getItemsByOwner(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findAllByText(String text) {
        text = text.toLowerCase();
        return itemDao.findAllByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        userService.existsUserById(userId);
        Item item = itemDao.findById(itemDto.getId());
        if (!item.getOwnerId().equals(userId)) {
            log.warn("Указан неверный userID: {}", userId);
            throw new NotFoundException("Предмет с id: " + item.getId() + " не найден");
        }

        Item itemFromMap = itemMapper.toItem(findById(item.getId()), userId);
        Item itemFromDto = itemMapper.toItem(itemDto, userId);

        itemFromMap.setName(Objects.requireNonNullElse(itemFromDto.getName(), itemFromMap.getName()));
        itemFromMap.setDescription(Objects.requireNonNullElse(itemFromDto.getDescription(), itemFromMap.getDescription()));
        itemFromMap.setAvailable(Objects.requireNonNullElse(itemFromDto.getAvailable(), itemFromMap.getAvailable()));

        return itemMapper.toItemDto(itemDao.update(itemFromMap));
    }

    @Override
    public void existsItemById(Long itemId) {
        if (!itemDao.existsItemById(itemId)) {
            throw new NotFoundException("Предмет с id: " + itemId + " не найден");
        }
    }

}
