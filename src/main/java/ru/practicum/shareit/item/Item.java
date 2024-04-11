package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ITEMS", schema = "PUBLIC")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;
    @Column(name = "ITEM_NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "AVAILABLE")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User owner;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                '}';
    }
}
