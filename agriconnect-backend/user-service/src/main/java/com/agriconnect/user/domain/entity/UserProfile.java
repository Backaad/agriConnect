package com.agriconnect.user.domain.entity;

import com.agriconnect.commons.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String city;

    @Column
    private String avatarUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_languages", joinColumns = @JoinColumn(name = "user_id",
        referencedColumnName = "user_id"))
    @Column(name = "language", length = 50)
    @Builder.Default
    private List<String> languages = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_specialties", joinColumns = @JoinColumn(name = "user_id",
        referencedColumnName = "user_id"))
    @Column(name = "specialty", length = 100)
    @Builder.Default
    private List<String> specialties = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
