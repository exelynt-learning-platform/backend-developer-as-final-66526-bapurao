package com.example.booking.controller;

import com.example.booking.dto.resource.*;
import com.example.booking.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
	private final ResourceService service;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceCreateRequest r) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
	}

	@GetMapping
	public Page<ResourceResponse> list(@PageableDefault(size = 20, sort = "name") Pageable p) {
		return service.list(p);
	}

	@GetMapping("/{id}")
	public ResourceResponse get(@PathVariable Long id) {
		return service.get(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceUpdateRequest r) {
		return service.update(id, r);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
