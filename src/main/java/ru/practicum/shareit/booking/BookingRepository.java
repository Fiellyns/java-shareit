package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    Collection<Booking> findAllByBookerId(long userId, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(long userId, LocalDateTime now,
                                                                           LocalDateTime localDateTime, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartTimeAfter(long userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByBookerIdAndEndTimeBefore(long userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatus(long userId, Status status, Sort sort);

    Collection<Booking> findAllByItemOwnerId(long userId, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(long userId, LocalDateTime start,
                                                                              LocalDateTime end, Sort sort);

    Collection<Booking> findFAllByItemOwnerIdAndStartTimeAfter(long userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndEndTimeBefore(long userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndStatus(long userId, Status status, Sort sort);

    Collection<Booking> findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
            Long userId, Long itemId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusEqualsAndStartTimeIsBefore(long itemId, Status status,
                                                                           LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusEqualsAndStartTimeIsAfter(long itemId, Status status,
                                                                          LocalDateTime time, Sort sort);

}
