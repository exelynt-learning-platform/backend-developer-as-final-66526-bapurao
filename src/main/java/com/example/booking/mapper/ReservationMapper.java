package com.example.booking.mapper;

import com.example.booking.dto.reservation.*;
import com.example.booking.entity.Reservation;

public final class ReservationMapper {
	private ReservationMapper() {
	}

	public static ReservationResponse toResponse(Reservation r) {
		return new ReservationResponse(r.getId(), r.getResource().getId(), r.getResource().getName(),
				r.getUser().getId(), r.getUser().getUsername(), r.getStartTime(), r.getEndTime(), r.getPrice(),
				r.getStatus());
	}
}
