package com.example.booking.dto.reservation;

import com.example.booking.entity.ReservationStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ReservationUpdateRequest(@NotNull @Positive Long resourceId, @NotNull LocalDateTime startTime,
		@NotNull LocalDateTime endTime, @NotNull ReservationStatus status) {
}
