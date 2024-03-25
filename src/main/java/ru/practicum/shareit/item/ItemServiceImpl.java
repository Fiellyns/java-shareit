package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDao;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
    public ItemDto create(ItemDto itemDto, Long userId) {
        userDao.existsUserById(userId);
        return itemMapper.toItemDto(itemDao.create(itemMapper.toItem(itemDto, userId)));
    }

    @Override
    public ItemDto findById(Long itemId) {
        itemDao.existsItemById(itemId);
        return itemMapper.toItemDto(itemDao.findById(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(Long userId) {
        userDao.existsUserById(userId);
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
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userDao.existsUserById(userId);
        itemDao.existsItemById(itemId);
        Item item = itemDao.findById(itemId);
        if (!item.getOwnerId().equals(userId)) {
            log.warn("Указан неверный userID: {}", userId);
            throw new UserNotFoundException("id: " + userId);
        }

        Item itemFromMap = itemMapper.toItem(findById(itemId), userId);
        Item itemFromDto = itemMapper.toItem(itemDto, userId);

        itemFromMap.setName(Objects.requireNonNullElse(itemFromDto.getName(), itemFromMap.getName()));
        itemFromMap.setDescription(Objects.requireNonNullElse(itemFromDto.getDescription(), itemFromMap.getDescription()));
        itemFromMap.setAvailable(Objects.requireNonNullElse(itemFromDto.getAvailable(), itemFromMap.getAvailable()));

        return itemMapper.toItemDto(itemDao.update(itemFromMap, itemId));
    }

}
