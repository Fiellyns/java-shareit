package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long idNext = 1L;

    private Long getIdNext() {
        return idNext++;
    }

    @Override
    public Item create(Item item) {
        item.setId(getIdNext());
        itemMap.put(item.getId(), item);
        log.info("Добавлен новый предмет: {}", item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public Collection<Item> getItemsByOwner(Long userId) {
        log.info("Возвращены все предметы пользователя с id: {}", userId);
        return itemMap.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findAllByText(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = itemMap.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(Collectors.toList());
            log.info("Возвращены все предметы по слову: {}.", text);
        }
        return searchItems;
    }

    @Override
    public Item update(Item item, Long itemId) {
        item.setId(item.getId());
        itemMap.put(item.getId(), item);
        log.info("Предмет с id: {}, был обновлён.", itemId);
        return item;
    }

    @Override
    public void existsItemById(Long itemId) {
        if (!itemMap.containsKey(itemId)) {
            log.warn("Предмет с id: {} не найден", itemId);
            throw new ItemNotFoundException("id: " + itemId);
        }
    }

}
