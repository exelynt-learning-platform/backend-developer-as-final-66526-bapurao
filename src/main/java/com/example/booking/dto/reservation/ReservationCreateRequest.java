package com.example.booking.dto.reservation;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public record ReservationCreateRequest(@NotNull @Positive Long resourceId,@NotNull LocalDateTime startTime,@NotNull LocalDateTime endTime) {}
