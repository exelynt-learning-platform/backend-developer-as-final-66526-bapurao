package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="users", uniqueConstraints={@UniqueConstraint(name="uk_user_username",columnNames="username"),@UniqueConstraint(name="uk_user_email",columnNames="email")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=50) private String username;
 @Column(nullable=false,length=150) private String email;
 @Column(nullable=false) private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role;
 @Column(nullable=false,updatable=false) private LocalDateTime createdAt;
 @Column(nullable=false) private LocalDateTime updatedAt;
 @PrePersist void prePersist(){ createdAt=LocalDateTime.now(); updatedAt=createdAt; }
 @PreUpdate void preUpdate(){ updatedAt=LocalDateTime.now(); }
}
