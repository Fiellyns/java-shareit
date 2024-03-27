package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemMapper itemMapper, ItemDao itemDao, UserDao userDao) {
        this.itemMapper = itemMapper;
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        return itemMapper.toItemDto(itemDao.create(itemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto findById(Long itemId) {
        return itemMapper.toItemDto(itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден")));
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(long userId) {
        User user = userDao.findById(userId) // проверка на наличие
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
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
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Item item = itemDao.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemDto.getId() + " не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Предмет с id: " + item.getId() + " не найден");
        }

        Item itemFromMap = itemMapper.toItem(findById(item.getId()), user);
        Item itemFromDto = itemMapper.toItem(itemDto, user);

        itemFromMap.setName(Objects.requireNonNullElse(itemFromDto.getName(), itemFromMap.getName()));
        itemFromMap.setDescription(Objects.requireNonNullElse(itemFromDto.getDescription(), itemFromMap.getDescription()));
        itemFromMap.setAvailable(Objects.requireNonNullElse(itemFromDto.getAvailable(), itemFromMap.getAvailable()));

        return itemMapper.toItemDto(itemDao.update(itemFromMap));
    }
}
