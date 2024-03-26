package ru.practicum.shareit.item;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                '}';
    }
}
