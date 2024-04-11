package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("select i from Item i " +
            "where (lower(i.name) like '%'||lower(:text)||'%' " +
            "or lower(i.description) like '%'||lower(:text)||'%') and i.available = true")
    Collection<Item> search(@Param("text") String text);
}
