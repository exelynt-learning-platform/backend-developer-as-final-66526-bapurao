package com.example.booking.service;

import com.example.booking.dto.reservation.*;
import com.example.booking.entity.ReservationStatus;
import org.springframework.data.domain.*;

public interface ReservationService {
	ReservationResponse create(ReservationCreateRequest r, String username);

	Page<ReservationResponse> list(String username, boolean admin, ReservationStatus status,
			java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Pageable p);

	ReservationResponse get(Long id, String username, boolean admin);

	ReservationResponse update(Long id, ReservationUpdateRequest r, String username, boolean admin);

	void delete(Long id, String username, boolean admin);
}
