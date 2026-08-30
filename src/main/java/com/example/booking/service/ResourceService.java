package com.example.booking.service;

import com.example.booking.dto.resource.*;
import org.springframework.data.domain.*;

public interface ResourceService {
	ResourceResponse create(ResourceCreateRequest r);

	Page<ResourceResponse> list(Pageable p);

	ResourceResponse get(Long id);

	ResourceResponse update(Long id, ResourceUpdateRequest r);

	void delete(Long id);
}
