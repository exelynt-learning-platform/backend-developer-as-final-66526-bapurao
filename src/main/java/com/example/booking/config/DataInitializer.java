package com.example.booking.config;


import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.ResourceType;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
	@Bean
	CommandLineRunner seed(UserRepository users, ResourceRepository resources, ReservationRepository reservations,
			PasswordEncoder encoder) {
		return args -> {
			if (!users.existsByUsername("admin")) {
				users.save(User.builder().username("admin").email("admin@example.com")
						.password(encoder.encode("admin123")).role(Role.ADMIN).build());
			}
			if (!users.existsByUsername("user")) {
				users.save(User.builder().username("user").email("user@example.com").password(encoder.encode("user123"))
						.role(Role.USER).build());
			}
			if (resources.count() == 0) {
				resources.save(Resource.builder().name("Conference Room A").description("10-person meeting room")
						.type(ResourceType.ROOM).price(new BigDecimal("500.00")).available(true).build());
				resources.save(Resource.builder().name("Company Car").description("Pool vehicle")
						.type(ResourceType.VEHICLE).price(new BigDecimal("1500.00")).available(true).build());
				resources.save(Resource.builder().name("Projector").description("HD projector")
						.type(ResourceType.EQUIPMENT).price(new BigDecimal("300.00")).available(true).build());
			}
			if (reservations.count() == 0) {
				User u = users.findByUsername("user").orElseThrow();
				Resource r = resources.findAll().get(0);
				reservations.save(Reservation.builder().user(u).resource(r)
						.startTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0))
						.endTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0))
						.price(r.getPrice()).status(ReservationStatus.CONFIRMED).build());
			}
		};
	}
}
