package com.agriconnect.user.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.user.domain.entity.UserProfile;
import com.agriconnect.user.domain.entity.UserRole;
import com.agriconnect.user.dto.request.UpdateProfileRequest;
import com.agriconnect.user.dto.response.PublicProfileResponse;
import com.agriconnect.user.dto.response.UserProfileResponse;
import com.agriconnect.user.mapper.UserMapper;
import com.agriconnect.user.repository.UserProfileRepository;
import com.agriconnect.user.repository.UserRoleRepository;
import com.agriconnect.user.service.UserProfileService;
import com.agriconnect.user.storage.S3AvatarStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRoleRepository roleRepository;
    private final UserMapper userMapper;
    private final S3AvatarStorage s3AvatarStorage;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil", userId.toString()));

        UserProfileResponse response = userMapper.toProfileResponse(profile);
        Set<String> roles = roleRepository.findByUserId(userId).stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());
        response.setRoles(roles);
        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).firstName("").lastName("").build());

        userMapper.updateFromRequest(request, profile);
        profile = profileRepository.save(profile);
        log.info("Profil mis à jour: userId={}", userId);

        return getMyProfile(userId);
    }

    @Override
    @Transactional
    public String updateAvatar(UUID userId, MultipartFile file) throws IOException {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil", userId.toString()));

        if (profile.getAvatarUrl() != null) {
            s3AvatarStorage.deleteAvatar(profile.getAvatarUrl());
        }

        String avatarUrl = s3AvatarStorage.uploadAvatar(userId, file);
        profile.setAvatarUrl(avatarUrl);
        profileRepository.save(profile);
        log.info("Avatar mis à jour: userId={}", userId);
        return avatarUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(UUID userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil", userId.toString()));
        return userMapper.toPublicResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PublicProfileResponse> searchProfiles(String query, Pageable pageable) {
        Page<UserProfile> page = profileRepository.search(query, pageable);
        Page<PublicProfileResponse> mapped = page.map(userMapper::toPublicResponse);
        return PageResponse.from(mapped);
    }
}
