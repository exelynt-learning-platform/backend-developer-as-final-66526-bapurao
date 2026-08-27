package com.example.booking.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.ResourceType;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Value("${app.seed.user-password}")
    private String userPassword;

    @Bean
    CommandLineRunner seed(
            UserRepository users,
            ResourceRepository resources,
            ReservationRepository reservations,
            PasswordEncoder encoder) {

        return args -> {

            // Create default ADMIN user
            if (!users.existsByUsername("admin")) {
                users.save(
                    User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(encoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build()
                );
            }

            // Create default USER
            if (!users.existsByUsername("user")) {
                users.save(
                    User.builder()
                        .username("user")
                        .email("user@example.com")
                        .password(encoder.encode(userPassword))
                        .role(Role.USER)
                        .build()
                );
            }

            // Create default resources
            if (resources.count() == 0) {

                resources.save(
                    Resource.builder()
                        .name("Conference Room A")
                        .description("10-person meeting room")
                        .type(ResourceType.ROOM)
                        .price(new BigDecimal("500.00"))
                        .available(true)
                        .build()
                );

                resources.save(
                    Resource.builder()
                        .name("Company Car")
                        .description("Pool vehicle")
                        .type(ResourceType.VEHICLE)
                        .price(new BigDecimal("1500.00"))
                        .available(true)
                        .build()
                );

                resources.save(
                    Resource.builder()
                        .name("Projector")
                        .description("HD projector")
                        .type(ResourceType.EQUIPMENT)
                        .price(new BigDecimal("300.00"))
                        .available(true)
                        .build()
                );
            }

            // Create sample reservation
            if (reservations.count() == 0) {

                User user = users.findByUsername("user")
                        .orElseThrow();

                Resource resource = resources.findAll()
                        .get(0);

                LocalDateTime startTime = LocalDateTime.now()
                        .plusDays(1)
                        .withHour(10)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                LocalDateTime endTime = LocalDateTime.now()
                        .plusDays(1)
                        .withHour(12)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

                reservations.save(
                    Reservation.builder()
                        .user(user)
                        .resource(resource)
                        .startTime(startTime)
                        .endTime(endTime)
                        .price(resource.getPrice())
                        .status(ReservationStatus.CONFIRMED)
                        .build()
                );
            }
        };
    }
}