package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="resources") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resource {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String name;
 @Column(length=1000) private String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private ResourceType type;
 @Column(nullable=false,precision=15,scale=2) private BigDecimal price;
 @Column(nullable=false) private boolean available;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt;
 @Column(nullable=false) private LocalDateTime updatedAt;
 @PrePersist void prePersist(){createdAt=LocalDateTime.now();updatedAt=createdAt;}
 @PreUpdate void preUpdate(){updatedAt=LocalDateTime.now();}
}
