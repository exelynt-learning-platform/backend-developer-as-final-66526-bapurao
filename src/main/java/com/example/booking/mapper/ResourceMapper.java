package com.example.booking.mapper;

import com.example.booking.dto.resource.*;
import com.example.booking.entity.Resource;

public final class ResourceMapper {
	private ResourceMapper() {
	}

	public static ResourceResponse toResponse(Resource r) {
		return new ResourceResponse(r.getId(), r.getName(), r.getDescription(), r.getType(), r.getPrice(),
				r.isAvailable());
	}
}
