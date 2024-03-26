package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemDaoImpl implements ItemDao {

    private final Map<Long, Item> itemMap = new HashMap<>();

    private final Map<Long, List<Item>> userItemsMap = new HashMap<>();
    private Long idNext = 1L;

    private Long getIdNext() {
        return idNext++;
    }

    @Override
    public Item create(Item item) {
        item.setId(getIdNext());
        itemMap.put(item.getId(), item);
        List<Item> userItemsList = userItemsMap.getOrDefault(item.getOwnerId(), new ArrayList<>());
        userItemsList.add(item);
        userItemsMap.put(item.getOwnerId(), userItemsList);
        log.info("Добавлен новый предмет: {}", item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public Collection<Item> getItemsByOwner(long userId) {
        log.info("Возвращены все предметы пользователя с id: {}", userId);
        return userItemsMap.getOrDefault(userId, Collections.emptyList());
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
    public Item update(Item item) {
        List<Item> userItemsList = userItemsMap.getOrDefault(item.getOwnerId(), new ArrayList<>());
        userItemsList.set(userItemsList.indexOf(itemMap.get(item.getId())), item);
        userItemsMap.put(item.getOwnerId(), userItemsList);
        itemMap.put(item.getId(), item);
        log.info("Предмет с id: {}, был обновлён.", item.getId());
        return item;
    }

    @Override
    public boolean existsItemById(Long itemId) {
        return itemMap.containsKey(itemId);
    }

}
