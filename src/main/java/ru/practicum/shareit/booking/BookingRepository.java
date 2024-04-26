package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
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

    List<Booking> findAllByBookerId(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(long userId, LocalDateTime now,
                                                                           LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartTimeAfter(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndTimeBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long userId, Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(long userId, LocalDateTime start,
                                                                              LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartTimeAfter(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndTimeBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, Status status, Pageable pageable);

    Collection<Booking> findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
            Long userId, Long itemId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusEqualsAndStartTimeIsBefore(long itemId, Status status,
                                                                           LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusEqualsAndStartTimeIsAfter(long itemId, Status status,
                                                                          LocalDateTime time, Sort sort);

}
