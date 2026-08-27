package com.example.booking.service;

import com.example.booking.dto.reservation.ReservationCreateRequest;
import com.example.booking.dto.reservation.ReservationUpdateRequest;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.ResourceType;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.exception.ReservationConflictException;
import com.example.booking.exception.UnauthorizedException;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.impl.ReservationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservationServiceImplTest {

    @Mock
    ReservationRepository reservations;

    @Mock
    ResourceRepository resources;

    @Mock
    UserRepository users;

    @InjectMocks
    ReservationServiceImpl service;

    User alice;
    User bob;
    Resource room;
    Reservation bobReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        alice = User.builder()
                .id(1L)
                .username("alice")
                .email("a@x.com")
                .role(Role.USER)
                .build();

        bob = User.builder()
                .id(2L)
                .username("bob")
                .email("b@x.com")
                .role(Role.USER)
                .build();

        room = Resource.builder()
                .id(10L)
                .name("Room")
                .type(ResourceType.ROOM)
                .price(new BigDecimal("500.00"))
                .available(true)
                .build();

        bobReservation = Reservation.builder()
                .id(100L)
                .user(bob)
                .resource(room)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .price(room.getPrice())
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    @Test
    void userCannotReadAnotherUsersReservation() {

        when(reservations.findById(100L))
                .thenReturn(Optional.of(bobReservation));

        assertThrows(
                UnauthorizedException.class,
                () -> service.get(100L, "alice", false)
        );
    }

    @Test
    void userCannotUpdateAnotherUsersReservation() {

        when(reservations.findById(100L))
                .thenReturn(Optional.of(bobReservation));

        ReservationUpdateRequest req =
                new ReservationUpdateRequest(
                        10L,
                        LocalDateTime.now().plusDays(2),
                        LocalDateTime.now().plusDays(2).plusHours(1),
                        ReservationStatus.CANCELLED
                );

        assertThrows(
                UnauthorizedException.class,
                () -> service.update(100L, req, "alice", false)
        );
    }

    @Test
    void userCannotDeleteAnotherUsersReservation() {

        when(reservations.findById(100L))
                .thenReturn(Optional.of(bobReservation));

        assertThrows(
                UnauthorizedException.class,
                () -> service.delete(100L, "alice", false)
        );

        // Alice is not the owner, so deleteById must NEVER be called.
        verify(reservations, never()).deleteById(anyLong());
    }

    @Test
    void overlappingReservationIsRejected() {

        when(users.findByUsername("alice"))
                .thenReturn(Optional.of(alice));

        when(resources.findById(10L))
                .thenReturn(Optional.of(room));

        when(
                reservations.existsConflict(
                        eq(10L),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        isNull()
                )
        ).thenReturn(true);

        ReservationCreateRequest req =
                new ReservationCreateRequest(
                        10L,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(1)
                );

        assertThrows(
                ReservationConflictException.class,
                () -> service.create(req, "alice")
        );

        verify(reservations, never()).save(any(Reservation.class));
    }
}