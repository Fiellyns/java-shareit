package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Item> userItemsList = userItemsMap.getOrDefault(item.getOwner().getId(), new ArrayList<>());
        userItemsList.add(item);
        userItemsMap.put(item.getOwner().getId(), userItemsList);
        return item;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(itemMap.get(itemId));
    }

    @Override
    public Collection<Item> getItemsByOwner(long userId) {
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
        }
        return searchItems;
    }

    @Override
    public Item update(Item item) {
        List<Item> userItemsList = userItemsMap.getOrDefault(item.getOwner().getId(), new ArrayList<>());
        userItemsList.set(userItemsList.indexOf(itemMap.get(item.getId())), item);
        userItemsMap.put(item.getOwner().getId(), userItemsList);
        itemMap.put(item.getId(), item);
        return item;
    }

}
