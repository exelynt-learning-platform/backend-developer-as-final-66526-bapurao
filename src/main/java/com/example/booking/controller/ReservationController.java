package com.example.booking.controller;

import com.example.booking.dto.reservation.*;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService service;

	@PostMapping
	public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationCreateRequest r,
			Authentication a) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r, a.getName()));
	}

	@GetMapping
	public Page<ReservationResponse> list(@RequestParam(required = false) ReservationStatus status,
			@RequestParam(required = false) BigDecimal minPrice, @RequestParam(required = false) BigDecimal maxPrice,
			@PageableDefault(size = 20, sort = "startTime") Pageable p, Authentication a) {
		boolean admin = a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
		return service.list(a.getName(), admin, status, minPrice, maxPrice, p);
	}

	@GetMapping("/{id}")
	public ReservationResponse get(@PathVariable Long id, Authentication a) {
		return service.get(id, a.getName(), isAdmin(a));
	}

	@PutMapping("/{id}")
	public ReservationResponse update(@PathVariable Long id, @Valid @RequestBody ReservationUpdateRequest r,
			Authentication a) {
		return service.update(id, r, a.getName(), isAdmin(a));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, Authentication a) {
		service.delete(id, a.getName(), isAdmin(a));
		return ResponseEntity.noContent().build();
	}

	private boolean isAdmin(Authentication a) {
		return a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
	}
}
