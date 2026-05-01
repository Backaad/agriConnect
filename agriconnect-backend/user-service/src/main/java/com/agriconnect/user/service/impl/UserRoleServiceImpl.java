package com.agriconnect.user.service.impl;

import com.agriconnect.commons.exception.ConflictException;
import com.agriconnect.user.domain.entity.UserRole;
import com.agriconnect.user.domain.enums.Role;
import com.agriconnect.user.repository.UserRoleRepository;
import com.agriconnect.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(UUID userId) {
        return roleRepository.findByUserId(userId).stream()
                .map(UserRole::getRole)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void addRole(UUID userId, Role role) {
        if (roleRepository.existsByUserIdAndRole(userId, role)) {
            throw new ConflictException("L'utilisateur possède déjà le rôle: " + role.name());
        }
        roleRepository.save(UserRole.builder().userId(userId).role(role).build());
        log.info("Rôle {} ajouté à userId={}", role, userId);
    }

    @Override
    @Transactional
    public void removeRole(UUID userId, Role role) {
        roleRepository.deleteByUserIdAndRole(userId, role);
        log.info("Rôle {} retiré de userId={}", role, userId);
    }
}
