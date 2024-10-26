package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    @Query("""
            Select b
            From Booking as b
            WHERE b.booker.id = :userId
            AND current_timestamp between b.start and b.end
            ORDER BY b.start DESC
            """
    )
    List<Booking> getAllByBookerIdAndCurrentTime(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime time, LocalDateTime time2);

    @Query("""
            Select b
            From Booking as b
            WHERE b.booker.id = :userId
            AND current_timestamp < b.end
            ORDER BY b.start DESC
            """)
    List<Booking> getAllByBookerIdAndPastTime(Long userId);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    @Query("""
            Select b
            From Booking as b
            WHERE b.booker.id = :userId
            AND current_timestamp < b.start
            ORDER BY b.start DESC
            """)
    List<Booking> getAllByBookerIdAndFutureTime(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime time);

    List<Booking> findAllByItemIdAndEndBefore(Long itemId, LocalDateTime time);

    List<Booking> getAllByBookerIdAndStatusIs(Long userId, Status status);

    List<Booking> findAllByItemOwnerIdAndStatusIsOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByIdInAndEndBefore(List<Long> itemsId, LocalDateTime time);

    List<Booking> getALLByItemIdAndBookerIdAndStatusIsOrderByEndDesc(Long userId, Long itemId, Status status);


}
