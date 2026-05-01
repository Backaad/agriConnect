package com.agriconnect.user.service;

import com.agriconnect.user.domain.enums.Role;

import java.util.Set;
import java.util.UUID;

public interface UserRoleService {

    Set<Role> getUserRoles(UUID userId);

    void addRole(UUID userId, Role role);

    void removeRole(UUID userId, Role role);
}
