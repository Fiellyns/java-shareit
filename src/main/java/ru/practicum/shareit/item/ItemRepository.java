package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("select i from Item i " +
            "where (lower(i.name) like '%'||lower(:text)||'%' " +
            "or lower(i.description) like '%'||lower(:text)||'%') and i.available = true")
    List<Item> search(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requests);

    List<Item> findAllByRequestId(Long requestId);
}
