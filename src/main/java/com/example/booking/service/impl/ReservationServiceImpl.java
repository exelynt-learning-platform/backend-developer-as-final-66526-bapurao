package com.example.booking.service.impl;

import com.example.booking.dto.reservation.*;
import com.example.booking.entity.*;
import com.example.booking.exception.*;
import com.example.booking.mapper.ReservationMapper;
import com.example.booking.repository.*;
import com.example.booking.service.ReservationService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
	private final ReservationRepository reservations;
	private final ResourceRepository resources;
	private final UserRepository users;

	private User user(String n) {
		return users.findByUsername(n).orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private void times(LocalDateTime s, LocalDateTime e) {
		if (!s.isBefore(e))
			throw new InvalidReservationException("Start time must be before end time");
		if (s.isBefore(LocalDateTime.now()))
			throw new InvalidReservationException("Start time cannot be in the past");
	}

	private void conflict(Long rid, LocalDateTime s, LocalDateTime e, Long exclude) {
		if (reservations.existsConflict(rid, s, e, exclude))
			throw new ReservationConflictException("Resource is already reserved for the requested time period");
	}

	@Transactional
	public ReservationResponse create(ReservationCreateRequest r, String username) {
		times(r.startTime(), r.endTime());
		User u = user(username);
		Resource x = resources.findById(r.resourceId())
				.orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + r.resourceId()));
		if (!x.isAvailable())
			throw new ResourceUnavailableException("Resource is currently unavailable");
		conflict(x.getId(), r.startTime(), r.endTime(), null);
		return ReservationMapper
				.toResponse(reservations.save(Reservation.builder().user(u).resource(x).startTime(r.startTime())
						.endTime(r.endTime()).price(x.getPrice()).status(ReservationStatus.CONFIRMED).build()));
	}
	
	@Transactional(readOnly = true)
	public Page<ReservationResponse> list(String username, boolean admin, ReservationStatus status, BigDecimal min,
			BigDecimal max, Pageable p) {
		Specification<Reservation> s = (root, q, cb) -> {
			List<Predicate> ps = new ArrayList<>();
			if (!admin)
				ps.add(cb.equal(root.get("user").get("username"), username));
			if (status != null)
				ps.add(cb.equal(root.get("status"), status));
			if (min != null)
				ps.add(cb.greaterThanOrEqualTo(root.get("price"), min));
			if (max != null)
				ps.add(cb.lessThanOrEqualTo(root.get("price"), max));
			return cb.and(ps.toArray(Predicate[]::new));
		};
		return reservations.findAll(s, p).map(ReservationMapper::toResponse);
	}

	private Reservation owned(Long id, String username, boolean admin) {
		Reservation r = reservations.findById(id)
				.orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id));
		if (!admin && !r.getUser().getUsername().equals(username))
			throw new UnauthorizedException("You do not have access to this reservation");
		return r;
	}
	
	@Transactional(readOnly = true)
	public ReservationResponse get(Long id, String username, boolean admin) {
		return ReservationMapper.toResponse(owned(id, username, admin));
	}

	@Transactional
	public ReservationResponse update(Long id, ReservationUpdateRequest req, String username, boolean admin) {
		Reservation r = owned(id, username, admin);
		if (r.getStatus() == ReservationStatus.CANCELLED)
			throw new InvalidReservationException("Cancelled reservations cannot be modified");
		times(req.startTime(), req.endTime());
		Resource x = resources.findById(req.resourceId())
				.orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + req.resourceId()));
		if (!x.isAvailable() && !x.getId().equals(r.getResource().getId()))
			throw new ResourceUnavailableException("Resource is currently unavailable");
		if (!admin && req.status() != ReservationStatus.CANCELLED && req.status() != r.getStatus())
			throw new UnauthorizedException("USER cannot change reservation status except cancellation");
		conflict(x.getId(), req.startTime(), req.endTime(), r.getId());
		r.setResource(x);
		r.setStartTime(req.startTime());
		r.setEndTime(req.endTime());
		r.setPrice(x.getPrice());
		r.setStatus(req.status());
		return ReservationMapper.toResponse(reservations.save(r));
	}

	@Transactional
	public void delete(Long id, String username, boolean admin) {
		reservations.delete(owned(id, username, admin));
	}
}
