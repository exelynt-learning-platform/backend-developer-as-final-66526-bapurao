package com.example.booking.dto.resource;
import com.example.booking.entity.ResourceType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ResourceCreateRequest(@NotBlank @Size(max=120) String name,@Size(max=1000) String description,@NotNull ResourceType type,@NotNull @PositiveOrZero BigDecimal price,boolean available) {}
