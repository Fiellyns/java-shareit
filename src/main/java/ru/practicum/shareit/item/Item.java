package ru.practicum.shareit.item;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", ownerId=" + ownerId +
                ", requestId=" + requestId +
                '}';
    }
}
