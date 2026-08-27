package com.example.booking.dto.resource;
import com.example.booking.entity.ResourceType;
import java.math.BigDecimal;
public record ResourceResponse(Long id,String name,String description,ResourceType type,BigDecimal price,boolean available) {}
