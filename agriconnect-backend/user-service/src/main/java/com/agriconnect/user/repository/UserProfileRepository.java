package com.agriconnect.user.repository;

import com.agriconnect.user.domain.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT p FROM UserProfile p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<UserProfile> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM UserProfile p WHERE p.region = :region")
    Page<UserProfile> findByRegion(@Param("region") String region, Pageable pageable);
}
