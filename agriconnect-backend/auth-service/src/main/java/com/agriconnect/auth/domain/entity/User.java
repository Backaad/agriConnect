package com.agriconnect.auth.domain.entity;

import com.agriconnect.auth.domain.enums.UserStatus;
import com.agriconnect.commons.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_phone", columnList = "phone"),
    @Index(name = "idx_users_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_auth_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role", length = 30)
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Column
    private LocalDateTime lastLoginAt;

    public void addRole(String role) {
        this.roles.add(role);
    }

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
}
