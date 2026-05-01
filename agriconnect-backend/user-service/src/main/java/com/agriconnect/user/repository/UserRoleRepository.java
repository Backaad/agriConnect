package com.agriconnect.user.repository;

import com.agriconnect.user.domain.entity.UserRole;
import com.agriconnect.user.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByUserId(UUID userId);

    Optional<UserRole> findByUserIdAndRole(UUID userId, Role role);

    boolean existsByUserIdAndRole(UUID userId, Role role);

    void deleteByUserIdAndRole(UUID userId, Role role);
}
